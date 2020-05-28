var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var admin = require("firebase-admin");
var FCM = require("fcm-push");
var serverKey = '---';
var fcm = new FCM(serverKey);


var userFriendRequests = (io) => {
  io.on('connection', function(socket){
    console.log(`Client ${socket.id} is connected to friendsServices`);
    sendOrDeleteFriendRequest(socket, io);
    approveOrDeclineFriendRequest(socket, io);
    detectDisconnection(socket, io);
    sendMessage(socket,io);
  });
};

function sendMessage(socket, io) {
  socket.on('details', (data)=> {
    var db = admin.database();
    var friendMessageRef = db.ref('userMessages')
    .child(encodeEmail(data.friendEmail))
    .child(encodeEmail(data.senderEmail))
    .child(data.messageId)
    var newFriendMessagesRef = db.ref('userNewMessages')
    .child(encodeEmail(data.friendEmail))
    .child(data.messageId);

    var chatRoomRef = db.ref('userChatRoom').child(encodeEmail(data.friendEmail)).child(encodeEmail(data.senderEmail));

    var message = {
      messageId:data.messageId,
      messageText:data.messageText,
      messageSenderEmail:data.senderEmail,
      messageSenderPicture:data.senderPicture
    };
    var chatRoom = {
      friendPicture: data.senderPicture,
      friendName: data.senderName,
      friendEmail: data.senderEmail,
      lastMessage: data.messageText,
      lastMessageSenderEmail: data.senderEmail,
      lastMessageRead: false,
      sentLastMessage: true
    };
    chatRoomRef.set(chatRoom);
    friendMessageRef.set(message);
    newFriendMessagesRef.set(message);

    var tokenRef = db.ref('userToken');
    var friendToken = tokenRef.child(encodeEmail(data.friendEmail));
    friendToken.once('value', (snapshot)=> {
      var message = {
        to:snapshot.val().token,
        data:{
          title: 'New Message',
          body: `${data.senderName}: ${data.messageText}`,
          image: `${data.senderPicture}`
        }
      };
      fcm.send(message)
      .then((response)=> {
        console.log('Message Sent!');
      }).catch((err)=>{
        console.log(err);
      });
    });
  })
}

function approveOrDeclineFriendRequest(socket, io) {
  socket.on('friendRequestResponse', (data)=> {
    var friendEmail = data.friendEmail;
    var userEmail = data.userEmail;
    var requestCode = data.requestCode;
    var userPicture = data.userPicture;

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
        });
      });

      var tokenRef = db.ref('userToken');
      var friendToken = tokenRef.child(encodeEmail(friendEmail));

      friendToken.once('value', (snapshot)=> {
        var message = {
          to:snapshot.val().token,
          data:{
            title: 'Friend Request Accepted',
            body: `Your request has been accepted by ${userEmail}`,
            image: `${userPicture}`
          }
        };
        fcm.send(message)
        .then((response)=> {
          console.log('Message Sent!');
        }).catch((err)=>{
          console.log(err);
        });
      });
    } else {

    }
  })
}

function sendOrDeleteFriendRequest(socket, io) {
  socket.on('friendRequest', (data) => {
    var friendEmail = data.email;
    var userEmail = data.userEmail;
    var requestCode = data.requestCode;
    var userPicture = data.userPicture;

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
        });
      });

      var tokenRef = db.ref('userToken');
      var friendToken = tokenRef.child(encodeEmail(friendEmail));

      friendToken.once('value', (snapshot)=> {
        var message = {
          to:snapshot.val().token,
          data:{
            title: 'Friend Request',
            body: `You have recieved a new friend request from ${userEmail}`,
            image: `${userPicture}`
          }
        };
        fcm.send(message)
        .then((response)=> {
          console.log('Message Sent!');
        }).catch((err)=>{
          console.log(err);
        });
      });

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
