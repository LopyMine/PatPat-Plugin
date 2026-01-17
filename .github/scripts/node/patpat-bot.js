import mineflayer from 'mineflayer'
import { Buffer } from 'buffer'
import { EventEmitter } from 'events'

const PAT_PACKET_V1_S2C = 'patpat:pat_entity_s2c_packet'
const PAT_PACKET_V1_C2S = 'patpat:pat_entity_c2s_packet'

const PAT_PACKET_V2_C2S = 'patpat:pat_entity_c2s_packet_v2'
const PAT_PACKET_V2_S2C = 'patpat:pat_entity_s2c_packet_v2'

const HELLO_PACKET_V2_S2C = 'patpat:hello_patpat_player_s2c_packet'
const HELLO_PACKET_V2_C2S = 'patpat:hello_patpat_server_c2s_packet'

class Utils {
  static longsToUuid(msb, lsb) {
    const hex = (msb << 64n | (lsb & 0xFFFFFFFFFFFFFFFFn))
      .toString(16)
      .padStart(32, '0')
    return [
      hex.slice(0, 8),
      hex.slice(8, 12),
      hex.slice(12, 16),
      hex.slice(16, 20),
      hex.slice(20)
    ].join('-')
  }

  static uuidToLongs(uuid) {
    const hex = uuid.replace(/-/g, '')
    const msb = BigInt('0x' + hex.slice(0, 16))
    const lsb = BigInt('0x' + hex.slice(16))
    return { msb, lsb }
  }

  static readVarInt(buf, offset = 0) {
    let num = 0
    let shift = 0
    let byte
    let bytesRead = 0

    do {
      byte = buf[offset + bytesRead]
      num |= (byte & 0x7F) << shift
      shift += 7
      bytesRead++
    } while (byte & 0x80)

    return { value: num, size: bytesRead }
  }

  static writeVarInt(value) {
    const buf = []
    do {
      let byte = value & 0x7F
      value >>>= 7
      if (value !== 0) byte |= 0x80
      buf.push(byte)
    } while (value !== 0)
    return Buffer.from(buf)
  }
}

class PatPatBot extends EventEmitter {
  constructor(username, options = {}) {
    super()
    this.username = username
    this.options = {
      host: options.host || 'localhost',
      port: options.port || 25565,
      ...options
    }
    this.bot = null
    this.packetQueue = new Map()
  }

  async connect() {
    return new Promise((resolve, reject) => {
      this.bot = mineflayer.createBot({
        host: this.options.host,
        port: this.options.port,
        username: this.username
      })

      this.bot.once('spawn', () => {
        this.onSpawn()
        resolve(this)
      })

      this.bot.once('error', reject)

      this.setupPacketListener()
    })
  }

  printStatus() {
    console.log(`Bot ${this.username} statuses:
      \tentityId: ${this.getEntityId()}
      \tuuid: ${this.getUuid()}
      \tcoordinates: ${this.bot.entity.position}`)
  }

  onSpawn() {
    // Override in child classes
  }

  setupPacketListener() {
    this.bot._client.on('packet', (data, meta) => {
      if (meta.name !== 'custom_payload' || !data.channel.startsWith('patpat')) return
      this.handlePacket(data)
      this.emit(`packet:${this.username}:${data.channel}`, data)
    })
  }

  handlePacket(data) {
    // Override in child classes
  }

  async waitPacket(channel, timeout = 5000) {
    return new Promise((resolve, reject) => {
      const timer = setTimeout(() => {
        this.removeListener(`packet:${this.username}:${channel}`, handler)
        reject(new Error(`[${this.username}] Timeout waiting for packet: ${channel}`))
      }, timeout)

      const handler = (data) => {
        clearTimeout(timer)
        resolve(data)
      }

      this.once(`packet:${this.username}:${channel}`, handler)
    })
  }

  async waitMessage(timeout = 5000, ...strings) {
    return new Promise((resolve, reject) => {
      const timer = setTimeout(() => {
        this.bot.removeListener('chat', handler)
        reject(new Error(`[${this.username}] Timeout waiting for message with strings: ${strings}`))
      }, timeout)

      const handler = (username, message) => {

        for (const str of strings) {
          if (!message.includes(str)) {
            return
          }
        }
        clearTimeout(timer)
        resolve({
          username,
          message
        })
      }

      this.bot.on('chat', handler)
    })
  }

  getUuid() {
    return this.bot?.player?.uuid
  }

  getEntityId() {
    return this.bot?.entity?.id
  }

  disconnect() {
    if (this.bot) {
      this.bot.quit()
    }
  }
}

class PatPatBotV1 extends PatPatBot {
  onSpawn() {
    this.bot._client.registerChannel(PAT_PACKET_V1_S2C, ['varlong', []], true)
  }

  handlePacket(data) {
    switch (data.channel) {
      case PAT_PACKET_V1_S2C:
        const patted = Utils.longsToUuid(
          data.data.readBigUInt64BE(0),
          data.data.readBigUInt64BE(8)
        )
        const whoPatted = Utils.longsToUuid(
          data.data.readBigUInt64BE(16),
          data.data.readBigUInt64BE(24)
        )
        console.log(`[${this.username}] Received pat: patted=${patted}, whoPatted=${whoPatted}`)
        this.emit('pat', { patted, whoPatted })
        break
    }
  }

  sendPat(targetUuid) {
    if (!this.bot) throw new Error('Bot not connected')

    const targetUuidLongs = Utils.uuidToLongs(targetUuid)

    const buffer = Buffer.allocUnsafe(16)
    buffer.writeBigUInt64BE(targetUuidLongs.msb, 0)
    buffer.writeBigUInt64BE(targetUuidLongs.lsb, 8)

    this.bot._client.write('custom_payload', {
      channel: PAT_PACKET_V1_C2S,
      data: buffer
    })

    console.log(`[${this.username}] Sent pat to UUID: ${targetUuid}`)
  }
}

class PatPatBotV2 extends PatPatBot {
  constructor(username, options = {}) {
    super(username, options)
    this.serverVersion = null
  }

  onSpawn() {
    this.bot._client.registerChannel(PAT_PACKET_V2_S2C, ['varint', []], true)
    this.bot._client.registerChannel(HELLO_PACKET_V2_S2C, ['ByteArray', []], true)
  }

  handlePacket(data) {
    switch (data.channel) {
      case HELLO_PACKET_V2_S2C:
        this.serverVersion = `${data.data[0]}.${data.data[1]}.${data.data[2]}`
        console.log(`[${this.username}] Received patpat server version: ${this.serverVersion}`)

        this.bot._client.write('custom_payload', {
          channel: HELLO_PACKET_V2_C2S,
          data: Buffer.of(1, 2, 5)
        })
        this.emit('hello', { version: this.serverVersion })
        break

      case PAT_PACKET_V2_S2C:
        const patted = Utils.readVarInt(data.data, 0)
        const whoPatted = Utils.readVarInt(data.data, patted.size)
        console.log(`[${this.username}] Received pat v2: patted=${patted.value}, whoPatted=${whoPatted.value}`)
        this.emit('pat', { patted: patted.value, whoPatted: whoPatted.value })
        break
    }
  }

  sendPat(targetEntityId) {
    if (!this.bot) throw new Error('Bot not connected')

    const targetId = Utils.writeVarInt(targetEntityId)

    this.bot._client.write('custom_payload', {
      channel: PAT_PACKET_V2_C2S,
      data: targetId
    })

    console.log(`[${this.username}] Sent pat v2 to entity ID: ${targetEntityId}`)
  }

  getServerVersion() {
    return this.serverVersion
  }
}

export {
  PAT_PACKET_V1_S2C,
  PAT_PACKET_V1_C2S,
  PAT_PACKET_V2_C2S,
  PAT_PACKET_V2_S2C,
  HELLO_PACKET_V2_S2C,
  HELLO_PACKET_V2_C2S,
  PatPatBotV1,
  PatPatBotV2
}