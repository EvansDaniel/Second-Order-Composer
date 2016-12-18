import oscP5.*;
import netP5.*;

OscP5 oscP5;
Visuals visuals;
NetAddress myBroadcastLocation; 

float xD, yD, zD;
int sX, sY, sZ;
boolean b = false, l = false;
PImage stars, earth, astrd;
int ptsW, ptsH;

void setup() {
  
  size(1000, 1000, P3D);

  stars = loadImage("stars.jpg");
  stars.resize(width, height);
  earth = loadImage("earth.jpg");
  astrd = loadImage("astrd.jpg");
  smooth(8);
  background(0);
  noStroke();
  noLoop();
  frameRate(25);
  oscP5 = new OscP5(this,12000);
  myBroadcastLocation = new NetAddress("127.0.0.1",12001);
}


void draw() {
  background(0);
  visuals = new Visuals();
  fill(255);
  text("Press the keys q through k (top left to bottom right on keyboard) for rad beats",0,25);
  text("If it's no sound is coming out, check you caps lock",0,50);
}

// Send OSC Message 
void mousePressed() {
  /* create a new OscMessage with an address pattern, in this case /test. */
  OscMessage myOscMessage = new OscMessage("/test");
  /* add a value (an integer) to the OscMessage */
  myOscMessage.add(1);
  /* send the OscMessage to a remote location specified in myNetAddress */
  oscP5.send(myOscMessage, myBroadcastLocation);
}
// Determines which OSC message to send 
void keyPressed() {
  OscMessage m;
  switch(key) {
    case('c'):
      /* connect to the broadcaster */
      m = new OscMessage("/server/connect",new Object[0]);
      oscP5.flush(m,myBroadcastLocation);  
      break;
    case('d'):
      /* disconnect from the broadcaster */
      m = new OscMessage("/server/disconnect",new Object[0]);
      oscP5.flush(m,myBroadcastLocation);  
      break;

  }  
  
  sendDrumMessage();
}

// Keys between 'q' and 'k' lowercase from top left of keyboard to bottom right 
void sendDrumMessage() {
  
  /* create a new OscMessage with an address pattern, in this case /test. */
  OscMessage myOscMessage = new OscMessage("/test");
  if( key == 'q')
    myOscMessage.add(1);
  else if( key == 'w') 
    myOscMessage.add(2);
  else if( key == 'e')
    myOscMessage.add(3);
  else if( key == 'r')
    myOscMessage.add(4);
  else if( key == 't')
    myOscMessage.add(5);
  else if( key == 'y') 
    myOscMessage.add(6);   
  else if( key == 'u')
    myOscMessage.add(7);
  else if( key == 'i')
    myOscMessage.add(8);
  else if( key == 'o')
    myOscMessage.add(9);
    else if( key == 'p')
    myOscMessage.add(10);
  else if( key == 'a')
    myOscMessage.add(11);
  else if( key == 's')
    myOscMessage.add(12);
  else if( key == 'd')
    myOscMessage.add(13);
    else if( key == 'f')
    myOscMessage.add(14);
  else if( key == 'g')
    myOscMessage.add(15);
  else if( key == 'h')
    myOscMessage.add(16);
  else if( key == 'j')
    myOscMessage.add(17);
    else if( key == 'k')
    myOscMessage.add(18);
  else if( key == 'l')
    myOscMessage.add(19);
    
  /* send the OscMessage to a remote location specified in myNetAddress */
  oscP5.send(myOscMessage, myBroadcastLocation); 
  
}


/* incoming osc message are forwarded to the oscEvent method. */
void oscEvent(OscMessage theOscMessage) {
  /* get and print the address pattern and the typetag of the received OscMessage */
  println("### received an osc message with addrpattern "+theOscMessage.addrPattern()+" and typetag "+theOscMessage.typetag());
  theOscMessage.print();
}
