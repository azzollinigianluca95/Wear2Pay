<!DOCTYPE html>
<meta charset="ISO-8859-1">
<html>
  <head>
    <title>Wear2Pay</title>
    <link rel="shortcut icon" href="img/favicon.ico" />
    <link rel="stylesheet" type="text/css" href="style.css">
  </head>
  <body>
    <div id="form_div">

      <div id="image_div">
        <img src="img/logo_CerchioMarrone.png" alt="LOGO"/>
      </div>

      <form action="insert_user.php" method="post">
        <span id="form_title"> Wear2Pay </span>
        <div class="input_container">
          <input class="input0" type="text" name="name" placeholder="First Name" required/>
        </div>
        <div class="input_container">
          <input class="input0" type="text" name="surname" placeholder="Last Name" required/>
        </div>
        <div class="input_container">
          <input class="input0" type="text" name="email" placeholder="Email" required/>
        </div>
        <div class="input_container">
          <input class="input0" type="text" name="login_name" placeholder="Username" required/>
        </div>
        <div class="input_container">
          <input class="input0" type="password" name="password" placeholder="Password" required/>
        </div>
        <input class="submit" type="submit" value="Subscribe!">
      </form>

    </div>
  </body>
</html>
