var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var admin = require("firebase-admin");

var firebaseCredential = require(__dirname + '/private/serviceCredentials.json');

admin.initializeApp({
  credential: admin.credential.cert(firebaseCredential),
  databaseURL: "https://leoclub-abc88.firebaseio.com"
});

io.on('connection', function(socket){
  console.log(`Client ${socket.id} is connected`);

  socket.once('disconnect', function(){
      console.log('A client has disconnected');
  });
});

http.listen(3000, ()=>{
  console.log('Server is listening to port 3000');
});
