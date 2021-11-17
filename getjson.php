<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

    $stmt = $con->prepare('select * from pm_info');
    $stmt->execute();

    if ($stmt->rowCount() > 0) {
        $data = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)) {
            extract($row);

            array_push($data,
                array('id'=>$id,
                    'name'=>$name,
                    'address'=>$address,
                    'latitude'=>$latitude,
                    'longitude'=>$longitude,
                    'photo'=>$photo,
                    'gcooter'=>$gcooter,
                    'deer'=>$deer,
                    'beam'=>$beam,
                    'talang'=>$talang,
                    'kickboard'=>$kickboard,
                    'bicycle'=>$bicycle
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("webnautes"=>$data),
        JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>