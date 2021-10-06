import { env } from 'process'
import express from 'express'
import { randomBytes } from 'crypto'

const port = 'HIT_COUNTER_PORT' in env ? env.HIT_COUNTER_PORT : 8080
const hostname = 'HOSTNAME' in env ? env.HOSTNAME : 'localhost'
const serverID = randomBytes(8).toString('hex') // 8-char random string

const server = express()

let counter = 0
 
server.get('/', function (req, res) {
  res.send(`[${serverID}@${hostname}:${port}] Hit ${++counter} times`)
})
 
console.log(`Service ${serverID} listening on ${hostname}:${port}`)
server.listen(port)
