// content of index.js
var fs = require('fs');
const http = require('http')  
const port = 80


const requestHandler = (request, response) => {  
  //console.log(request.url)
  response.writeHead(200, {"Content-Type": "application/json"});
  var responseObj = {
	  "message" : "ok"
  }
  response.end(JSON.stringify(responseObj));
}

const server = http.createServer(requestHandler)

server.listen(port, (err) => {  
  if (err) {
    return console.log('something bad happened', err)
  }

  //console.log(`server is listening on ${port}`)
})