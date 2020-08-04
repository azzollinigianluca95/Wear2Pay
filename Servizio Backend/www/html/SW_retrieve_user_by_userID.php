<?php

$host = '127.0.0.1';
$username = 'g_azzollini';
$pwd='Juveworld1a.';
$db = "wear2pay";

$conn=mysqli_connect($host, $username, $pwd, $db) or die('Unable to connect');

if(mysqli_connect_error($conn))
{
    echo "Failed to Connect to database".mysqli_connect_error();
}

$user_id = $_POST["user_id"];

$response =array();
$sql = "SELECT
 u.name
 ,u.surname
,w.amount
 
FROM
 users  as u INNER JOIN
wallet as w ON w.user_id = u.id
WHERE
 u.id = '$user_id' ";

$result= mysqli_query($conn,$sql);

if(mysqli_num_rows($result) > 0)
{
 	   while ($row = mysqli_fetch_assoc($result))
	{
        	array_push($response, $row);
	}

}

else {
  $response['success'] = 0;
  $response['message']= 'no data';
}

echo json_encode($response);
mysqli_close($conn);

?>

