

class Visuals {
  
 
 
 public Visuals() {
   
    clear();
    directionalLight(255,255,255,2,.5,0);
    translate(width/2, height/2, 0);
    if (!b){
     sphere(120);
    }
    else{
      textureSphere(120,120, 120, earth);
    }
    rotateX(xD);
    rotateY(yD);
    rotateZ(zD);
    translate(400, 0,0);
    if(b){
      texture(astrd);
    }
    if (!b){
     sphere(40);
    }
    else{
      textureSphere(40,40, 40, astrd);
    }
   
  } 
}
