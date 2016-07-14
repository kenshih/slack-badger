# badger

A Clojure app for hackathon

## Usage

hum dee dum... todo

## License

Copyright © 2016 Meetup/Ken Shih

## Notes
docker build -t badger-app .
docker run -p 3000:3000 --rm --name badger-running badger-app
IP_LOCAL_APP_CONTAINER=192.168.99.100
IP_LOCAL_APP_CONTAINER=(docker-machine ip default)
docker kill badger-running
curl http://192.168.99.100:3000/hi
docker run -p 3000:80 --rm --name badger-running badger-app
