var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var admin = require("firebase-admin");

var userAccountRequests = (io) => {
  io.on('connection', function(socket){
    console.log(`Client ${socket.id} is connected`);
    detectDisconnection(socket, io);
    registerUser(socket, io);
    logUserIn(socket, io);
  });
};

function logUserIn(socket, io) {
  socket.on('userInfo', (data)=> {
    admin.auth().getUserByEmail(data.email)
    .then((userRecord)=>{
      var db = admin.database();
      var ref = db.ref('users');
      var userRef = ref.child(encodeEmail(data.email));
      userRef.once('value', (snapshot)=> {
        var additionalClaims = {
          email: data.email
        };
        admin.auth().createCustomToken(userRecord.uid, additionalClaims)
        .then((customToken)=> {
          Object.keys(io.sockets.sockets).forEach((id) => {
            if(id == socket.id) {
              var token = {
                authToken: customToken,
                email: data.email,
                photo: snapshot.val().userPicture,
                displayName: snapshot.val().username,
                gender: snapshot.val().gender,
                about: snapshot.val().about,
                password: data.password
              }

              userRef.child('hasLoggedIn').set(true);
              socket.emit('token', token);
            }
          });
        })
        .catch((error)=> {
          Object.keys(io.sockets.sockets).forEach((id) => {
            if(id == socket.id) {
              var token = {
                authToken: error.message,
                email: 'error',
                photo: 'error',
                displayName: 'error'
              }
              socket.emit('token', {token});
            }
          });
        });
      });
    });
  })
}

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
      var userRef = ref.child(encodeEmail(data.email));
      var date = {
        date: admin.database.ServerValue.TIMESTAMP
      };

      userRef.set({
        email: data.email,
        username: data.username,
        userPicture: '',
        about: 'Hey there! I am using SMS application!',
        gender: 'Not yet assigned',
        dateJoined: date,
        hasLoggedIn: false,
        password: data.passwordHash
      });

      var emailVerificationDB = db.ref('emailVerification');
      var eVUserRef = emailVerificationDB.child(encodeEmail(data.email));
      eVUserRef.set({
        isEmailVerified: false
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

function encodeEmail(email) {
  return email.replace(/[.]/g,',');
}

module.exports = {
  userAccountRequests
}
