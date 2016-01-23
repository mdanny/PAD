# Modules
http = require('http')

data = JSON.stringify([
  {
    id: '1'
    username: 'first-user'
    password: 'first-pass'
  }
  {
    id: '2'
    username: 'second-user'
    password: 'second-pass'
  }
  {
    id: '3'
    username: 'third-user'
    password: 'third-pass'
  }
])

options =
  host: 'localhost'
  port: 8080
  path: '/sample-post'
  method: 'POST'
  headers:
    'Content-Type': 'application/x-www-form-urlencoded'
    'Content-Length': Buffer.byteLength(data)

req = http.request(options, (res) ->
  res.setEncoding 'utf8'
  res.on 'data', (chunk) ->
    console.log 'Request body has the following data: ' + chunk
    return
  return
)
req.write data
req.end()
