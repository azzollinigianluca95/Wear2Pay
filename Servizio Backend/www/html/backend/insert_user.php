<!DOCTYPE html>
<meta charset="ISO-8859-1">
<html>
<head>
  <title>Wear2Pay</title>
  <link rel="shortcut icon" href="img/favicon.ico" />
  <link rel="stylesheet" type="text/css" href="style2.css">
</head>
  <body>
    <?php
      function OpenCon()
       {
       $dbhost = "127.0.0.1";
       $dbuser = "g_azzollini";
       $dbpass = "Juveworld1a.";
       $db = "wear2pay";
       $conn = new mysqli($dbhost, $dbuser, $dbpass,$db) or die("Connect failed: %s\n". $conn -> error);

       return $conn;
       }

      function CloseCon($conn)
       {
       $conn -> close();
       }

       $conn = OpenCon();

       if ($conn->connect_error){
         die("Errore di connessione al database".$conn->connect_error);
       }

       $name = $_POST["name"];
       $surname = $_POST["surname"];
       $email =  $_POST["email"];
       $login_name =  $_POST["login_name"];
       $password = $_POST["password"];

       $insert_sql = "INSERT INTO `users`(`login_name`,`name`,`surname`, `email`, `password`) VALUES (?,?,?,?,?)";
       $check_username_sql = "SELECT * FROM `users` WHERE login_name = '$login_name'";
       $check_email_sql = "SELECT * FROM `users` WHERE email = '$email'";
       $select_user_id_sql = "SELECT `id` FROM `users` WHERE login_name = '$login_name'";
       $insert_wallet_sql = "INSERT INTO `wallet`(`user_id`, `amount`) VALUES (?,0)";
       $insert_recharge_account = "INSERT INTO `transaction`(`user_id`,`cost`, `product_id`) VALUES (?,10,0)";


       $already_exist = False;

       $result_user = $conn->query($check_username_sql);
       if($result_user->num_rows > 0) {
         $result_user->close();
         $already_exist = True;
         $_POST["check"] = "USERNAME".'<br>'."ALREADY USED";
       }

        $result_email = $conn->query($check_email_sql);


        if($result_email->num_rows > 0) {
          $result_email->close();
          $already_exist = True;
          $_POST["check"] = "EMAIL ALREADY".'<br>'."USED";
         }

         if ($already_exist == False) {
           $stmt = $conn->prepare($insert_sql);
           if ($stmt === False){
             echo "ERRORE NEL PREPARE";
           }
           $stmt->bind_param("sssss", $login_name, $name, $surname, $email, $password);
           $stmt->execute();
           if ($stmt === False){
             echo "ERRORE NEL EXECUTE";
           }

           $user_id_result = $conn->query($select_user_id_sql);
           $row = $user_id_result -> fetch_assoc();
           $user_id_ = $row['id'];

           $stmt1 = $conn->prepare($insert_wallet_sql);
           if ($stmt1 === False){
             echo "ERRORE NEL PREPARE AGGIUNTA PORTAFOGLIO";
           }
           $stmt1->bind_param("s", $user_id_);
           $stmt1->execute();
           if ($stmt1 === False){
             echo "ERRORE NELL'AGGIUNTA PORTAFOGLIO";
           }

	   $stmt2 = $conn->prepare($insert_recharge_account);
           if ($stmt2 === False){
             echo "ERRORE NEL PREPARE RICARICA PORTAFOGLIO";
           }
           $stmt2->bind_param("s", $user_id_);
           $stmt2->execute();
           if ($stmt2 === False){
             echo "ERRORE NELLA RICARICA PORTAFOGLIO";
           }

           $_POST["check"] = "SUCCESSFULLY".'<br>'."SUBSCRIBED!";
         }

       CloseCon($conn);

    ?>

    <div id="form_div">

      <div id="image_div">
        <img src="img/logo_CerchioMarrone.png" alt="LOGO"/>
      </div>

      <form action="home.php">
        <span id="form_title"> <?php echo $_POST["check"]; ?></span>
        <input class="submit" type="submit" value="Go back">
      </form>
    </div>
  </body>
</html>
