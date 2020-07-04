var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var admin = require("firebase-admin");

var firebaseCredential = require(__dirname + '/private/serviceCredentials.json');

admin.initializeApp({
  credential: admin.credential.cert(firebaseCredential),
  databaseURL: "https://leoclub-abc88.firebaseio.com"
});

var accountRequests = require('./firebase/account-services');
accountRequests.userAccountRequests(io);

var friendRequests = require('./firebase/friend-services');
friendRequests.userFriendRequests(io);

http.listen(process.env.PORT, ()=>{
  console.log('Server is listening to port');
});
