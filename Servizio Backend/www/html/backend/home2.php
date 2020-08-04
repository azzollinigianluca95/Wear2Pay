<!DOCTYPE html>
<html>
  <head>
    <title> My PHP own page! </title>
  </head>
  <body>
    <h1> Welcome! </h1>

    <?php echo "Eccalla!"; ?>
    <?php echo $_SERVER["PHP_SELF"]?>
    <?php echo $_SERVER["SERVER_NAME"]?>
    <?php
      $x = 1;
      echo $x;
    ?>
    <?php
      define("ITERATIONS",10);
      define("TEXT_ITER","Ciaone");

      function cicla(){
        for($i=0; $i < ITERATIONS; $i = $i+1){
          echo TEXT_ITER;
        }
      }
      cicla();
    ?>

  </body>
</html>
