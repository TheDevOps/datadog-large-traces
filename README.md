This is a small sample springboot application to show a problem of gigantic traces being recorded when using the microsoft azure servicebus library

In order to reproduce the issue the following steps are needed:

1) First of all configure a topic+subscription in your azure servicebus instance https://portal.azure.com/
1.1) Create Topic 
![image](https://github.com/user-attachments/assets/ce8bfa7b-89a9-48ab-b5fa-b06203b781a8)
1.2) Create Subscription
![image](https://github.com/user-attachments/assets/42a76dda-a389-4278-80e4-1e6ce2186798)
![image](https://github.com/user-attachments/assets/e3a9b512-f02b-4230-9c20-03ad1573c430)
1.3) Create READ_WRITE key and copy connection string
![image](https://github.com/user-attachments/assets/61c836bf-d5cc-45dd-91fe-902030059d5e)
Click on the created key and copy the primary connection string
![image](https://github.com/user-attachments/assets/5a9297ed-70e7-48d2-a062-6cf29d2a1167)

2) Clone and prepare the project
2.1) Once cloned edit the File at.porscheinformatik.datadog.config.TestConfiguration in line 39 and replace the connection string with the real one you copied
![image](https://github.com/user-attachments/assets/f86e3258-889e-416f-9244-9201282b59d9)
2.2) Build the maven app using `mvn clean install`

If you already can use the datadog agent from your local IDe just run the spring main class from there and all should be ready. If you want to build a docker image and deploy it in a datadog enabled openshift cluster see the following steps

3) Build and deploy docker image in openshift
3.1) Build docker image from root directory of the repository using `docker build -t your.registry/datadog-large-trace .` Obviously change "your.registry" as required
3.2) Push the image using `docker push `your.registry/datadog-large-trace` Once again adjust registry as needed
3.3) Finally edit the deployment.yaml file in line 98 to point to correct image registry and import it in openshift. Adjust datadog tracer versions and so on as needed. The yaml assumed datadog in openshift is applied by annotation instrumentation.


