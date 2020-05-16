var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var admin = require("firebase-admin");

var userAccountRequests = (io) => {
  io.on('connection', function(socket){
    console.log(`Client ${socket.id} is connected`);
    registerUser(socket, io);
    detectDisconnection(socket, io);
  });
};

function registerUser(socket, io) {
  socket.on('userData', (data) => {
    admin.auth().createUser({
      email: data.email,
      displayName: data.username,
      password: data.password
    })
    .then((userRecord)=> {
      console.log("user was registered successfully");
      var db = admin.database();
      var ref = db.ref('users');
      var userRef = ref.child(userRecord.uid);
      var date = {
        date: admin.database.ServerValue.TIMESTAMP
      };

      userRef.set({
        email: data.email,
        username: data.username,
        userPicture: 'https://genslerzudansdentistry.com/wp-content/uploads/2015/11/anonymous-user.png',
        dateJoined: date,
        hasLoggedIn: false,
        password: data.password
      });

      Object.keys(io.sockets.sockets).forEach((id) => {
        if(id == socket.id) {
          var message = {
            text: 'Success'
          }
          socket.emit('message', message);
        }
      });
    })
    .catch((error)=> {
      Object.keys(io.sockets.sockets).forEach((id) => {
        console.log(error.message);
        if(id == socket.id) {
          var message = {
            text: error.message
          }
          io.to(id).emit('message', message);
        }
      });
    });
  });
}

function detectDisconnection(socket, io) {
  socket.once('disconnect', function(){
      console.log('A client has disconnected');
  });
}

module.exports = {
  userAccountRequests
}
