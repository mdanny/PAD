http = require('http')
dispatcher = require('httpdispatcher')
url = require('url')
_ = require('underscore')

# The port server will listen to
PORT = 8080

# Declaring an array of objects as our data warehouse
data_warehouse = new Array

# We need a function which handles requests and sends responses

handleRequest = (request, response) ->
  try
    #Disptach
    dispatcher.dispatch request, response
  catch err
    console.log err
  dispatcher.setStatic 'resources'

  #A sample POST request
  dispatcher.onPost '/sample-post', (req, res) ->
    res.writeHead 200, 'Content-Type': 'text/plain'
    # response to confirm the data transmission
    res.end 'Server received Post Data' + req.body
    # Output the data
    console.log 'Data received from the nodes' + req.body
    data_warehouse.push.apply data_warehouse, JSON.parse(req.body)
    return

  #GET request /employee with the given ID
  dispatcher.onGet '/employee', (req, res) ->
    res.writeHead 200, 'Content-Type': 'text/plain'
    url_parts = url.parse(req.url, true)
    query = url_parts.query

    if _.isEmpty(query)
      res.end 'Please insert a valid id for the employee'
    else
      data_warehouse.forEach (arrayItem) ->
        x = arrayItem.id
        if query.id == x
          res.end JSON.stringify(arrayItem)
        return
    return

  # GET request a list of /employees
  dispatcher.onGet '/employees', (req, res) ->
    res.writeHead 200, 'Content-Type': 'text/plain'
    url_parts = url.parse(req.url, true)
    query = url_parts.query

    if _.isEmpty(query)
      # display all employees in the data warehouse
      res.end JSON.stringify(data_warehouse)
    else
      data_warehouse.forEach (arrayItem) ->
        if query.offset and query.limit
          temp = []
          i = 0
          while i < query.limit
            temp.push data_warehouse[query.offset - 1 + i]
            i++
          res.end JSON.stringify(temp)
        else
          res.end 'please give a correct url query'
        return
    return
  return

#Create a server
server = http.createServer(handleRequest)
#Lets start our server
server.listen PORT, ->
  #Callback triggered when server is successfully listening. Hurray!
  console.log 'Server listening on: http://localhost:%s', PORT
  return
