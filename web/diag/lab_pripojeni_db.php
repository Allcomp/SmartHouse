<?php
//HEADER
session_start();
Header("Cache-control: no-cache");
Header("Pragma: no-cache");
Header("Expires: 0");
Header("Content-Type: text/html; charset=Windows-1250");
extract($_REQUEST);
// -------------------------------------- db name
//$dness = Date("Y-m-d", Time());
//-------------------------------------------------------
//DB CONNECT
$server_name = "localhost"; // jm�no datab�zov�ho serveru - hostitel
//$db_name = "topeni"; // jm�no datab�ze
$pass = "123456"; // heslo
$user = "root"; // uzivatel
$db_name_smarthouse = "smarthouse";
$db_name_smart_control = "smart_control";
MySQL_Connect($server_name, $user, $pass) or die('Nepodarilo se pripojit k MySQL datab�zi'); // pripojen� k datab�zi
//******************************************************************************
?>
