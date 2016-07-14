# Kubernetes Training

### Section 1 - Quick Docker Review/Introduction

##### Mac

  - Do you need to install virtualbox on your machine?
    - `brew cask install virtualbox`
  - Do you need to install docker on your machine?
    - `brew install docker docker-machine docker-compose`
  - Lets start a docker-machine instance and connect to it
    - `docker-machine create --driver virtualbox default`
    - `eval $(docker-machine env default)`
      - You may want to add this to your bash profile

##### Ubuntu Linux

  - [Docker docs on adding their sources, install their docker-engine package](https://docs.docker.com/engine/installation/linux/ubuntulinux/)

##### Then both continue...

  - Let's see if docker is working?
    - `docker run hello-world`
    - (sudo if on linux)
  - Let's build a docker container locally
    - `make package`
  - Let's push it up to the registry
    - `make publish`
    

### Section 2 - Getting started with Google Compute
  - Installing gcloud and kubernetes
    - https://cloud.google.com/sdk/
    - `gcloud components install kubectl`
  - Logging in to gcloud
    - `gcloud auth login email@meetup.com`
  - Working with projects
    - `gcloud config set project meetup-dev`
  - Setting up your default zone
    - `gcloud config set compute/zone us-east1-b`
  - Logging into kubernetes
    - `gcloud config set container/cluster training-sandbox`
  - Get credentials - you will need an admin to grant permission
    - `gcloud container clusters get-credentials training-sandbox`

### Section 3 - Exploring Kubernetes
  - Finding your space
    - `kubectl get namespaces`
  - Take a look around
    - What pods are running in the example namespace?
      - `kubectl --namespace example get pods`
    - What deployments are running in the example namespace?
      - `kubectl --namespace example get deployments`
    - What replication sets are running in the example namespace?
      - `kubectl --namespace example get rs`
    - What services are running in the example namespace?
      - `kubectl --namespace example get svc`
    - What replica set is our deployment currently on?
      - `kubectl --namespace example describe deployment webserver`
      - Get the name of the NewReplicaSet
    - What image is that replica set using?
      - `kubectl --namespace example describe rs <NewReplicaSet name>`
    - What secrets are running in the kube-system namespace?
      - `kubectl --namespace kube-system get secrets`

### Section 4 - Adding a New Service
  - Create your own namespace
    - Modify your-ns.yaml to create a new namespace for yourself
    - Create your namespace
      - `kubectl create -f your-ns.yaml`
    - Lets confirm that your namespace has been created
      - `kubectl get namespaces`
  - Deploy the application
    - Modify webserver-dply.yaml to use your namespace in the namespace section and in the image section
    - Create your deployment
      - `kubectl apply -f webserver-dply.yaml`
    - Watch it deploy
      - `kubectl --namespace <your-ns> describe deployments webserver`
  - Connect it to the internet
    - Modify webserver-svc.yaml to use your namespace and connect to the pod you created.
    - Create your service
      - `kubectl create -f webserver-svc.yaml`
  - Behold your amazingness!!!
    - Get the LoadBalancer Ingress IP
      - `kubectl --namespace <your-ns> describe svc webserver`
      - This may take some time before it populates
    - `curl <loadbalancer ingress ip>`
    - Check the logs
      - `kubectl --namespace <your-ns> get pods`
      - `kubectl --namespace <your-ns> logs -f <pod-name>`
      - Try curling the website again and watch the logs in real time.

### Section 5 - Performing a Deployment
  - Change the index.html to display your adjective!
  - Let's set a BUILD_NUMBER so a new version will be published
    - `export BUILD_NUMBER=2`
  - Let's modify the Makefile to use your namespace
    - in the Makefile change `repo_name := example` to `repo_name := <your-ns>`
  - __package__ it up and __publish__ it
    - `make package publish`
  - Lets change the webserver-dply.yaml to use the new build number
    - Modify the image line in webserver-dply.yaml to use version 2
  - Let's redeploy!
    - `kubectl apply -f webserver-dply.yaml`
  - Watch it redeploy
    - `kubectl --namespace <your-ns> describe deployments webserver`
    - Keep running it, you can use watch if it's installed
      - Did you see the new instance get created?
      - How about the old instance terminated?
    - Curl the website again.
      - Did the website change?
