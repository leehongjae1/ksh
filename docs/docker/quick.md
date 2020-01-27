build
===
* Hardware
    - Raspberry Pi 3 B+
        + ***2.5A Adapter***
* OS
    - Raspbian Stretch with desktop - v 2018-11-13

# 1. Create directory

```bash
cd ~
mkdir app
mkdir mysql
```

| directory | description |
| --- | --- |
| app | web, api server. |
| mysql | exported database file |

# 2. Copy files

Copy files to `/home/pi` in a convenient way like `ftp` or `samba`

| directory/file | description |
| --- | --- |
| mqtt/subscriber/archive | MQTT subscriber |
| smartCradle.tar | api web application |

# 3. Install docker

> Try

```bash
curl -fsSL get.docker.com -o get-docker.sh && sh get-docker.sh
sudo gpasswd -a $USER docker
```

> Test

```bash
docker -v
```

# 4. Docker run containers

> MQTT Brocker

```bash
sudo docker pull pascaldevink/rpi-mosquitto
sudo docker run -dp 1883:1883 --restart unless-stopped --name=mqtt pascaldevink/rpi-mosquitto
```

| option | description |
| --- | --- |
| -d | run in background |
| -p 1883:1883| open MQTT port |
| --restart unless-stopped|auto start on boot|
| --name=mqtt| Naming |

> Database - MySQL

```bash
sudo docker pull hypriot/rpi-mysql
sudo docker run -dp 3306:3306 --restart unless-stopped -e MYSQL_ALLOW_EMPTY_PASSWORD=true --name=mysql -v /home/pi/mysql:/var/lib/mysql hypriot/rpi-mysql
```

| option | description |
| --- | --- |
| -d | run in background |
| -p 3306:3306| open MySQL port |
| --restart unless-stopped|auto start on boot|
| -e MYSQL_ALLOW_EMPTY_PASSWORD=true|root login without password|
| --name=mysql| Naming |
| -v /home/pi/mysql:/var/lib/mysql| export mysql data |

> Api Web Application

Must build on Raspberry PI

```bash
sudo docker load < smartCradle.tar
sudo docker run -dp 80:8080 --restart unless-stopped --name=smartCradle com.choistec.smartcradle/api
```

| option | description |
| --- | --- |
| -d | run in background |
| -p 80:8080| open Server port |
| --restart unless-stopped|auto start on boot|
| --name=smartCradle| Naming |

> MQTT Subscriber

```bash
sudo docker build -t subscriber archive
sudo docker run -d --restart unless-stopped --name=subscriber subscriber
```

| option | description |
| --- | --- |
| -d | run in background |
| --restart unless-stopped|auto start on boot|
| --name=subscriber| Naming |

> Test

```bash
sudo docker ps
```

# 5. Database Setting

> Create User & Database

```sql
create database smart_cradle;
use mysql;
CREATE USER 'choistec'@'%' IDENTIFIED BY 'chois2016!';
GRANT ALL ON *.* TO 'choistec'@'%';
flush privileges;
```

and run `core/mysql/src/main/resources/table.sql`