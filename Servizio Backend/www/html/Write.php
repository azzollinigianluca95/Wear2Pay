<?php

$host = '127.0.0.1';
$username = 'g_azzollini';
$pwd='Juveworld1a.';
$db = "wear2pay";

$con=mysqli_connect($host, $username, $pwd, $db) or die('Unable to connect');

if(mysqli_connect_error($con))
{
    echo "Failed to Connect to database".mysqli_connect_error();
}

$user_id = $_POST['user_id'] ?? '';
$cost= $_POST['cost'] ?? '';
$product_id=$_POST['product_id'] ?? '';

$sql="INSERT INTO transaction(user_id,cost, product_id) VALUES('$user_id','$cost','$product_id')";
$result= mysqli_query($con,$sql);

if($result)
{
    echo ('Successfully Saved');
}else
{
    echo('Not saved successfully');
}

mysqli_close($con);
?>

