/**
 * 
*/

function login(){
  window.location.href = "login.html";
  document.getElementById("loginInputs").style.display = "flex";
  
}

function changeToLogin(){
  document.getElementById("choiceForLogin").style.backgroundColor = "white";
  document.getElementById("choiceForSignup").style.backgroundColor = "#44B4B8";
  document.getElementById("loginInputs").style.display = "flex";
  document.getElementById("signupInputs").style.display = "none";
}

function changeToSignup(){
  document.getElementById("choiceForLogin").style.backgroundColor = "#44B4B8";
  document.getElementById("choiceForSignup").style.backgroundColor = "white";
  document.getElementById("loginInputs").style.display = "none";
  document.getElementById("signupInputs").style.display = "flex";
}

function changeborder(x){
   x.style.border = "2px solid black";
   document.getElementById("invalidLogin").style = "display : none";
   document.getElementById("invalidSignup").style = "display : none";
}

function validationForLogin(){
	var email = document.getElementById("loginEmail").value;
	var password = document.getElementById("loginPassword").value;	
  if(email.trim()=="" || password.trim()==""){
    if(email.trim()==""){
      var inp = document.getElementById("loginEmail");
      inp.style.border = "2px solid red";
    }
    if(password=="" || password.trim()==""){
      var inp = document.getElementById("loginPassword");     
      inp.style.border = "2px solid red";
    }    
    document.getElementById("invalidLogin").style = "display : block";
  }
  else{
    var xhr = new XMLHttpRequest();	               
	  xhr.onreadystatechange = () => {
		if(xhr.readyState == 4 && xhr.status == 200){
			var json = JSON.parse(xhr.responseText);
      if(json.StatusCode == 200){
        if(json.Role === "Admin"){
          document.cookie ="voterid=null";
          window.location.href = "admin.html";
        }
        else if(json.Role === "User"){
          window.location.href = "user.html";
        }
      }
      else if(json.StatusCode == 400){
        document.getElementById("invalidLogin").style = "display : block";
        document.getElementById("invalidLogin").innerHTML = "Authentication didn't work! Please try again!";
      }
      else if(json.StatusCode == 500){
        document.getElementById("invalidLogin").style = "display : block";
        document.getElementById("invalidLogin").innerHTML = "Unexpected error accured! Please contact system administrator!";
      }
		}
	}
  xhr.open("POST", "./Login");
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  xhr.send("email="+email+"&password="+password);
  }
}

function admin(){
  var xhr = new XMLHttpRequest();
  xhr.onreadystatechange = () => {
    if(xhr.readyState==4 && xhr.status==200){
       var json = JSON.parse(xhr.responseText);
       document.getElementById("adminName").innerHTML += "Name : "+json.name;
       document.getElementById("adminEmail").innerHTML += "Email : "+json.email;
    }  
  }
  xhr.open("GET", "./myapp/admin/Admin");
  xhr.send();
}

function validationForSignup(){ 
  var name = document.getElementById("userName").value;
  var password = document.getElementById("password").value;
  var email = document.getElementById("email").value;
  if(name.trim()=="" || email.trim()=="" || password.trim()==""){
    if(name.trim()==""){
      var inp = document.getElementById("userName");
      inp.style.border = "2px solid red";
    }
    if(password.trim()==""){
      var inp = document.getElementById("password");
      inp.style.border = "2px solid red";
    }
    if(email.trim()==""){
      var inp = document.getElementById("email");     
      inp.style.border = "2px solid red";
    }
    document.getElementById("invalidSignup").style = "display : block";
  }
  else{
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
      if(xhr.readyState == 4 && xhr.status == 200){
        var json = JSON.parse(xhr.responseText);
        if(json.StatusCode == 200){
          window.location.href = "user.html";
        }
        else if(json.StatusCode == 400){
          document.getElementById("invalidSignup").style = "display : block";
          document.getElementById("invalidSignup").innerHTML = "Invalid inputs! Please enter proper inputs!";
        }
        else if(json.StatusCode == 500){
          document.getElementById("invalidSignup").style = "display : block";
          document.getElementById("invalidSignup").innerHTML = "User with email already exists!";
        }
      }
    } 
    xhr.open("POST", "./SignUp");
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.send("userName="+name+"&password="+password+"&email="+email); 
  }
}

function logout(){
  console.log("logout");
  var xhr = new XMLHttpRequest();
  xhr.onreadystatechange = () => {
    if(xhr.readyState==4 && xhr.status==200){
      var json = JSON.parse(xhr.responseText);
      if(json.StatusCode==200){
        window.location.href = "index.html";
      }
    }
  }
  xhr.open("GET", "./myapp/Logout");
  xhr.send();
}

