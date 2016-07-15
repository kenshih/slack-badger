FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
EXPOSE 3000
EXPOSE 80
RUN /bin/bash -c 'echo "" > init.sh && chmod +x init.sh'
CMD apt-get update && apt-get -y install xvfb && export DISPLAY=\":1\" && Xvfb :1 -screen 0 1024x768x24 & export DISPLAY=":1" && sleep 40s && lein run
