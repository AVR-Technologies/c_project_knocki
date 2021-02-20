import 'dart:convert';
import 'package:flutter/material.dart';
import 'dart:io';
import 'package:assets_audio_player/assets_audio_player.dart';
import 'package:torch/torch.dart';

void main() => runApp( MaterialApp( title: 'Knocki', home: MyHomePage(),),);

class MyHomePage extends StatefulWidget {                                       //create stateful widget
  @override _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {                              //holds state of above widget
  bool              isConnected       = false;                                  //holds boolean telling if connected to tcp server or not
  Socket            socket;                                                     //create object for tcp client socket
  String            host              = '192.168.0.12';                          //server ip
  int               port              = 7777;                                   //server port
  AssetsAudioPlayer assetsAudioPlayer = new AssetsAudioPlayer();                //create object for audio player

  @override void dispose() {
    stop();
    super.dispose();
  }
  @override Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Knocki'),
      ),
      body: centerUi(),
    );
  }

  Widget centerUi(){
    return Center(//child in center
      child: Column(//adds children vertically
        crossAxisAlignment: CrossAxisAlignment.stretch,//children width full
        children: <Widget>[
          row1(),                                                                           //Connect disConnect button row
          row2(title: 'Bulb A', iconColor: Colors.purple[600], onData: "A", offData: "a"),  //bulb A
          row2(title: 'Bulb B', iconColor: Colors.red[600],    onData: "B", offData: "b"),  //bulb B
          fanTitleUi(),                                                                     //fan title
          fanSpeedControllerUI(),                                                           //fan controller row
        ],
      ),
    );
  }
  Widget row1(){
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,                         //space between and around evenly
      children: <Widget>[
        Text('$host:$port', textAlign: TextAlign.center,),
        TextButton(
          style: TextButton.styleFrom(
            backgroundColor: Colors.green[800],
            primary: Colors.white,
          ),
          child: Text('Connect'),
          onPressed: isConnected ? null : connect,                              // if connected disable button or call connect() on click
        ),
        TextButton(
          style: TextButton.styleFrom(
            primary: Colors.white,
            backgroundColor: Colors.red[800],
          ),
          child: Text('Disconnect'),
          onPressed: !isConnected ? null : disconnect,                          // if disconnected disable button or call disconnect() on click
        )
      ],
    );
  }
  Widget row2({@required String title, @required Color iconColor,@required String onData,@required String offData}){
    return Visibility(
      visible: isConnected,                                                     //if isConnected == true , visible, else hidden
      child: ListTile(
        leading: Icon(Icons.lightbulb_outline, color: iconColor ?? Colors.blue[800],),
        title: Text(title ?? 'Bulb'),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            TextButton(
              style: TextButton.styleFrom(
                primary: Colors.green[800],
              ),
              onPressed: () => send(onData),
              child: Text('On'),
            ),
            TextButton(
              style: TextButton.styleFrom(
                primary: Colors.red[800]
              ),
              onPressed: () => send(offData),
              child: Text('Off'),
            )
          ],
        ),
      ),
    );
  }
  Widget fanTitleUi(){
    return Visibility(
      visible: isConnected,                                                     //if isConnected == true , visible, else hidden
      child: ListTile(
        dense: true,
        title: Text('Fan Speed'),
      ),
    );
  }
  Widget fanSpeedControllerUI(){
    return Visibility(
      visible: isConnected,                                                     //if isConnected == true , visible, else hidden
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,                       //space evenly between and around children
        children: <Widget>[
          fanSpeedButton(titleText: '0', buttonColor: Colors.red[400],    messageText: 'f0'),
          fanSpeedButton(titleText: '1', buttonColor: Colors.amber[900],  messageText: 'f1'),
          fanSpeedButton(titleText: '2', buttonColor: Colors.yellow[800], messageText: 'f2'),
          fanSpeedButton(titleText: '3', buttonColor: Colors.lime[800],   messageText: 'f3'),
          fanSpeedButton(titleText: '4', buttonColor: Colors.green[800],  messageText: 'f4'),
        ],
      ),
    );
  }
  Widget fanSpeedButton({@required String titleText, @required Color buttonColor, @required String messageText}){
    return SizedBox(
      width: MediaQuery.of(context).size.width / 5 * 0.8,                       // (screen width/5)*80%
      child: TextButton(
        child: Text(titleText),
        style: TextButton.styleFrom(
          backgroundColor: buttonColor,
          primary: Colors.white
        ),
        onPressed: ()=> send(messageText),
      ),
    );
  }

  Widget alertDialogUi({@required String title, @required String message}){
    return AlertDialog(
      title: Text(title),
      content: Text(message),
      actions: <Widget>[
        TextButton(
          onPressed: stop,
          child: Text('Close'),
        ),
      ],
    );
  }

  //12ssrx,10sstx,2,3relay
  connect() async {
    socket = await Socket.connect(host, port);                                  //wait until connects to tcp server
    print('Connected');                                                         //prints connected for debugging
    setState(() => isConnected = true);                                         //refresh ui with isConnected = true
    listenForIncomingData();                                                    //call listen()
  }
  disconnect() async {
    await socket.close();                                                       //wait to close connection from tcp server
    socket.destroy();                                                           //destroy socket
    print("Disconnected");                                                      //print disconnected for debugging
    setState(() => isConnected = false);                                        //refresh ui with isConnected = false
  }

  listenForIncomingData(){
    socket.listen((event) {                                                     //listen for incoming event
      List<int> data = [];                                              //create blank List<int>
      data.add(event[0]);                                                       //add first byte of event to data
      String message = utf8.decode(data);                                       //utf8 decode data array to string
      print(message);                                                           //print message for debugging
      if(message == "p") mobileFound();                                         // if message is 'p' call mobileFound()
      else if(message == "k") knockOnTheDoor();                                 // else if message is 'l' call knockOnTheDoor()
    }).onDone(() => disconnect());                                              // on connection end call disconnect()
  }
  send(_message){
    socket.add(utf8.encode(_message));                                          //utf8 decode message to List<int> or byte array and send to server
  }

  mobileFound(){
    play();                                                                     //call play()
    showDialog(context: context, builder:(context) => alertDialogUi(title: 'Found', message: 'Close to stop music.'), barrierDismissible: false);
  }
  knockOnTheDoor() {
    play();                                                                     //call play()
    showDialog(context: context, builder:(context) => alertDialogUi(title: 'Knock Knock!', message: 'Someone is on the door.'), barrierDismissible: false);
  }

  play() {
    assetsAudioPlayer.open(Audio('assets/audios/iphone_6_original.mp3',),);                                                //play audio
    Torch.turnOn();                                                             //turn on torch
  }
  stop() {
    assetsAudioPlayer.stop();                                                   //stop audio
    Torch.turnOff();                                                            //turn off torch
    Navigator.pop(context);                                                     //close dialog
  }
}