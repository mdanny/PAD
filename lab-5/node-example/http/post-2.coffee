http = require('http')
data = JSON.stringify([
  {
    id: '11'
    username: 'fourth-user'
    password: 'fourth-pass'
  }
  {
    id: 'abcv'
    username: 'fifth-user'
    password: 'fifth-pass'
  }
  {
    id: '323d3'
    username: 'sixth-user'
    password: 'sixth-pass'
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
