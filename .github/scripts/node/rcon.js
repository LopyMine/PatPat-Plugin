import Rcon from 'rcon'

const default_option = {
    tcp: true,
    challenge: false
}

class MyRcon {
    constructor(address = 'localhost', port = 25575, password = '') {
        this.address = address
        this.port = port
        this.password = password
    }

    waitConnect(timeout = 90) {
        var self = this;

        return new Promise(function (resolve, reject) {
            var connected = false;
            var startTime = Date.now();

            function tryToConnect() {
                if (Date.now() - startTime > timeout * 1000) {
                    clearInterval(interval);
                    reject(new Error("Rcon cannot connect to server"));
                    return;
                }
                if (connected) return;
                console.log("Try to connect...");
                var conn = new Rcon(self.address, self.port, self.password, default_option);
                conn.on('auth', function () {
                    if (connected) return;
                    connected = true;
                    clearInterval(interval);
                    console.log("Authenticated");
                    self.conn = conn;
                    resolve(conn);
                });
                conn.on('error', function () { });

                conn.connect();
            }

            var interval = setInterval(tryToConnect, 3000);
            tryToConnect()
        });
    }

    async waitResponse(timeout = 5000, ...strings) {
        return new Promise((resolve, reject) => {
            const timer = setTimeout(() => {
                this.conn.removeListener('response', handler)
                reject(new Error(`[Rcon] Timeout waiting for response with strings: ${strings}`))
            }, timeout)

            const handler = (data) => {
                for (const str of strings) {
                    if (!data.includes(str)) {
                        return
                    }
                }
                clearTimeout(timer)
                resolve(data)
            }

            this.conn.once('response', handler)
        })
    }
}

export {
    MyRcon
}