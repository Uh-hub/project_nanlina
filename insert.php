<?php

    error_reporting(E_ALL);
    ini_set('display_errors', 1);

    include('dbcon.php');

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if ((($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android) {
        
        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다
        $useID=$_POST['useID'];
        $name=$_POST['name'];
        $pm_name=$_POST['pm_name'];
        $pm_type=$_POST['pm_type'];

        
        if(!isset($errMSG)) {
            try {
                // SQL문을 실행하여 데이터를 MySQL 서버의 user_info 테이블에 저장합니다
                $stmt = $con->prepare('insert into user_info(useID, name, pm_name, pm_type) values(:useID, :name, :pm_name, :pm_type)');
                $stmt->bindParam(':useID', $useID);
                $stmt->bindParam(':name', $name);
                $stmt->bindParam(':pm_name', $pm_name);
                $stmt->bindParam(':pm_type', $pm_type);

                if ($stmt->execute()) {
                    $successMSG = "새로운 사용자를 추가했습니다";
                }
                else {
                    $errMSG = "사용자 추가 에러";
                }
            } 
            catch (PDOException $e) {
                die("Database error: ". $e->getMessage());
            }
        }
    }
?>

<?php
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

?>