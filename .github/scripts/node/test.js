import { PatPatBotV1, PatPatBotV2, HELLO_PACKET_V2_S2C, PAT_PACKET_V1_S2C, PAT_PACKET_V2_S2C } from './patpat-bot.js'
import { MyRcon } from './rcon.js'

// Test pat work v2 <-> v2
async function test1(rcon) {
  try {
    const bot1 = new PatPatBotV2('Bot1')
    bot1.waitPacket(HELLO_PACKET_V2_S2C, 8000)
    await bot1.connect()
    bot1.printStatus()
    const bot2 = new PatPatBotV2('Bot2')
    bot2.waitPacket(HELLO_PACKET_V2_S2C, 2000)
    await bot2.connect()
    bot2.printStatus()

    rcon.conn.send('tp Bot2 Bot1')
    await rcon.waitResponse(1500, '')

    console.log('Send pat packet bot1 -> bot2')
    setTimeout(() => bot1.sendPat(bot2.getEntityId()), 1000)
    await bot2.waitPacket(PAT_PACKET_V2_S2C, 3000)
    console.log('Packet bot1 -> bot2: success')

    console.log('Send pat packet bot2 -> bot1')
    setTimeout(() => bot2.sendPat(bot1.getEntityId()), 100)
    await bot1.waitPacket(PAT_PACKET_V2_S2C, 3000)
    console.log('Packet bot2 -> bot1: success')

    console.log('Test 1 passed')
    bot1.bot.end()
    bot2.bot.end()
  } catch (error) {
    console.error('Error:', error)
    process.exit(1)
  }
}

// Test pat work v1 <-> v2
async function test2(rcon) {
  try {
    const bot1 = new PatPatBotV1('Bot_V1')
    await bot1.connect()
    const bot2 = new PatPatBotV2('Bot_V2')
    await bot2.connect()

    bot2.waitPacket(HELLO_PACKET_V2_S2C, 2000)
    rcon.conn.send('tp Bot_V2 Bot_V1')

    await rcon.waitResponse(1500, '')
    console.log('Hello packet received')
    console.log('Send pat packet bot1 -> bot2')
    setTimeout(() => bot1.sendPat(bot2.getUuid()), 100)
    bot1.waitMessage(2000, 'outdated')
    await bot2.waitPacket(PAT_PACKET_V2_S2C, 3000)
    console.log('Packet bot1 -> bot2: success')

    console.log('Send pat packet bot2 -> bot1')
    setTimeout(() => bot2.sendPat(bot1.getEntityId()), 100)
    await bot1.waitPacket(PAT_PACKET_V1_S2C, 3000)
    console.log('Packet bot2 -> bot1: success')

    console.log('Test 2 passed')
    bot1.bot.end()
    bot2.bot.end()
  } catch (error) {
    console.error('Error:', error)
    process.exit(1)
  }
}

console.log('RCON connect')
var rcon1 = new MyRcon('localhost', 25575, 'dockerserver')
await rcon1.waitConnect(90)

console.log('Server prepare')
setTimeout(() => rcon1.conn.send('patpat info'), 100)
if(process.argv.includes('--folia')){
  console.log('Skip plugin output test, because old folia server not support rcon')
} else {
  await rcon1.waitResponse(3000, 'Platform:', 'Minecraft Version:', 'Version:')
  console.log('Plugin work')
}

console.log("Test1: Test pat work v2 <-> v2")
await test1(rcon1)
console.log("Test2: Test pat work v1 <-> v2")
await test2(rcon1)

console.log('Tests passed')
setTimeout(() => {
  process.exit(0)
}, 4000)