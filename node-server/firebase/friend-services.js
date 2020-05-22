var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var admin = require("firebase-admin");

var userFriendRequests = (io) => {
  io.on('connection', function(socket){
    console.log(`Client ${socket.id} is connected to friendsServices`);
    sendOrDeleteFriendRequest(socket, io);
    approveOrDeclineFriendRequest(socket, io);
    detectDisconnection(socket, io);
  });
};

function approveOrDeclineFriendRequest(socket, io) {
  socket.on('friendRequestResponse', (data)=> {
    var friendEmail = data.friendEmail;
    var userEmail = data.userEmail;
    var requestCode = data.requestCode;

    var db = admin.database();
    var friendRef = db.ref('friendRequestSent')
    .child(encodeEmail(friendEmail))
    .child(encodeEmail(userEmail));
    friendRef.remove();

    if(requestCode == "0") {
      var db = admin.database();
      var ref = db.ref('users');
      var userRef = ref.child(encodeEmail(userEmail));

      var userFriendRef = db.ref('userFriends');
      var friendFriendRef = userFriendRef.child(encodeEmail(friendEmail)).child(encodeEmail(userEmail));

      userRef.once('value',(snapshot)=> {
        friendFriendRef.set({
          email: snapshot.val().email,
          username: snapshot.val().username,
          userPicture: snapshot.val().userPicture,
          dateJoined: snapshot.val().dateJoined,
          hasLoggedIn: snapshot.val().hasLoggedIn,
        })
      })
    } else {

    }
  })
}

function sendOrDeleteFriendRequest(socket, io) {
  socket.on('friendRequest', (data) => {
    var friendEmail = data.email;
    var userEmail = data.userEmail;
    var requestCode = data.requestCode;

    var db = admin.database();
    var friendRef = db.ref('friendRequestReceived')
    .child(encodeEmail(friendEmail))
    .child(encodeEmail(userEmail));

    if(requestCode == 0) {
      var db = admin.database();
      var ref = db.ref('users');
      var userRef = ref.child(encodeEmail(userEmail));

      userRef.once('value',(snapshot)=> {
        friendRef.set({
          email: snapshot.val().email,
          username: snapshot.val().username,
          userPicture: snapshot.val().userPicture,
          dateJoined: snapshot.val().dateJoined,
          hasLoggedIn: snapshot.val().hasLoggedIn,
        })
      })
    } else {
      friendRef.remove();
    }
  });
}

function detectDisconnection(socket, io) {
  socket.once('disconnect', function(){
      console.log('A client has disconnected from friendsServices');
  });
}

function encodeEmail(email) {
  return email.replace('.',',');
}

module.exports = {
  userFriendRequests
}
