<?php

    define('SQL_DSN',      'mysql:host=localhost;dbname=calendar-matcher;charset=utf8');
    define('SQL_USERNAME', 'root');
    define('SQL_PASSWORD', '');

	$db = new PDO(SQL_DSN, SQL_USERNAME, SQL_PASSWORD);


    function insert(PDO $db, $username,$password,$ip)
    {
        $q = $db->prepare('INSERT INTO user (username, password, ip) VALUES (:username,:password, :ip)');
        $q->bindValue(':username', $username, PDO::PARAM_STR);
        $q->bindValue(':password', $password, PDO::PARAM_STR);
        $q->bindValue(':ip', $ip, PDO::PARAM_STR);
        $q->execute();
    };

    function update(PDO $db, $username, $ip)
    {
        $q = $db->prepare('UPDATE user SET ip=:ip WHERE username = :username');
        // bind value des champs
        $q->bindValue(':username', $username, PDO::PARAM_STR);
        $q->bindValue(':ip', $ip, PDO::PARAM_STR);
        $q->execute();
    };

    function get_by_username(PDO $db, $username) {
        $s = $db->prepare('SELECT * FROM user where username = :l');
        $s->bindValue(':l', $username, PDO::PARAM_STR);
        $s->execute();
        $data = $s->fetch(PDO::FETCH_ASSOC);
        if ($data) {
            return $data;
        }
        else {
            return null;
        }
    };

    function get_all_user(PDO $db) {
        $s = $db->prepare('SELECT id, username, ip FROM user');
        $s->execute();
        $data = $s->fetchAll(PDO::FETCH_ASSOC);
        if ($data) {
            return $data;
        }
        else {
            return null;
        }
    };

    if (isset($_POST['username']) && isset($_POST['password']) && isset($_POST['ip'])) {
        $username = $_POST['username'];
        $password = $_POST['password'];
        $ip = $_POST['ip'];
        $user = get_by_username($db, $username);
        if ($user != null) {
            if ($password == $user['password']) {
                update($db, $username, $ip);
            }
        } else {
            insert($db, $username, $password, $ip);
        }
    }


    if (isset($_GET['username'])) {
        $username  = $_GET['username'];
        $user = get_by_username($db, $username);
        if ($user != null) {
            $data = array(
                "username" => $user['username'],
                "ip" => $user['ip']
            );
            header('Content-Type: application/json');
            echo json_encode($data);
        }
    }

    if (isset($_GET['all_user'])) {
        if ($_GET['all_user'] == 1) {
            $data = get_all_user($db);
            if ($data) {
                header('Content-Type: application/json');
                echo json_encode($data);
            }
        }
    }


?>
