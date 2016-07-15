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

## deployment steps
export BUILD_NUMBER=6
make package publish
## need to update version in here:
kubectl apply -f webserver-dply.yaml
kubectl --namespace badger-ken describe deployments webserver
curl http://104.196.11.217:3000/


{
    "mrkdwn": false,
    "text": "joe has earned \n http://www.gummyworm.net/wp-content/uploads/2015/02/Pinguino-png-129x129.png"
}
