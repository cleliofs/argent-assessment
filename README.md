
# argent-assessment

##### Task 1

Assume the core of our dapp is made of the 2 smart contracts defined in Appendix A: a simple Wallet contract and its associated WalletFactory. The factory has 2 public methods to create and change the ownership of an existing Wallet.

In addition, we offer a mobile application (the App) that stores an Ethereum account and interact with the Ethereum blockchain through a local Ethereum node.

To offer the Wallets to the users for free we provision the Wallets in batches of 100 and assign individual Wallets to the users as they install the App (after installation the App sends the address of its local Ethereum account to the backend).

Every time the pool of available Wallets is under a threshold (let’s say 10), a new batch of 100 Wallets should be provisioned.

Q: Propose a scalable architecture to provision and manage the Wallets based on AWS. Make sure your answer contains information about:
* The technologies you would use
* The public API exposed by the backend
* The flows for creating and assigning Wallets
* How we could prevent outages, monitor and respond to issues

Q: How would you secure the API and make sure that:
* Only authorized Apps from Argent can request a Wallet?
* The requester is the owner of the provided Ethereum account?


__Proposed architecture:__

First and foremost, one should modularise all services into micro-services where it facilitates the development by making easier and faster to develop, enabling innovation and accelerating time-to-market for new features. Independent services that communicate over pre-defined and programmable APIs interfaces, via REST API. That brings several benefits, as (not limited to): Agility, scalability, easy deployment, resilience, reusable code at architecture level via small and well integrated services, technological freedom where each services could potentially be writing in a different stack/language.

AWS can integrate building blocks (micro-services) that support any application architecture, regardless of scale, load, or complexity.   

Currently, AWS provides two well defined, consisted and well-proven microservices solutions that serve many production ready applications/systems. I would like to proposed and discuss those two different approaches, commonly used in distributed AWS cloud computing ecosystem:  


_(A)_ AWS ECS containers (EC2 instances):

AWS can provide container-based service that highly scalable, high performance container management service that supports Docker containers and allows you to easily run applications on a managed cluster of Amazon EC2 instances. Amazon ECS lets you easily build all types of containerized applications, from long-running applications and microservices to batch jobs and machine learning applications.

With that architecture in mind, a proposed AWS container-based scalable platform could run Java applications via Spring Boot micro-services exposing REST APIs integration. Each service would be compromised of several AWS EC2 instances running in a container (such as Docker) and they would sit behind a AWS ELB (Elastic Load Balance) services where traffic and load would be evenly distributed across all instances of the "cluster". That is acommon approach used by many AWS customers where they rely on the Elastic Load Balancing (ELB) Application Load Balancer together with Amazon EC2 Container Service (Amazon ECS) and Auto Scaling to implement a microservices application.

_Note:_ ELB automatically distributes incoming application traffic across multiple Amazon EC2 instances. That directly would tackle and prevent outages where EC2 instances can be easily drained and removed from active "cluster"/set of live instances. A monitoring via AWS CloudWatch can be fully set to check "liveness" of all running EC2 instances.

AWS ELB would interact with API Gateway Internet-facing gateway that would expose to end-users the hidden intranet (VPC - Virtual Private Cloud) back-end services. 

Also, in order to provide a better availability of services with minimum or zero-downtime (i.e. outages), the services running in docker containers can be located in different Availability Zones (AZ) and, in an unlikely, even of a crash of a certain AWS AZ region, the damage would be prevented once the services would be cross zones/regions. Amazon ECS container instances are scaled out and scaled in, depending on the load or the number of incoming requests. Elastic scaling allows the system to be run in a cost-efficient way and also helps protect against denial of service (DoS) attacks.

The diagram below illustrates the _(A)_ AWS ECS containers approach:

![Alt text](img/Architecture-A.png?raw=true "(A) AWS ECS containers")



_(B)_ Serverless Microservices:

AWS Lambda lets you run code without provisioning or managing servers. Just upload your code and Lambda manages everything that is required to run and scale your code with high availability.

AWS Lambdas would be triggers via HTTP requests coming from AWS API Gateway. Lambda is highly integrated with API Gateway. The possibility of making synchronous calls from API Gateway to AWS Lambda enables the creation of fully serverless applications. 

_Note:_ A small drawback from this approach is what is called "cold starts" for AWS Lambdas. Cold start execution has a direct impact on the code execution time of an application. 

__Here are some observations:__

* First, in both technologies, the cold start increases the execution time of the Lambda.
* Note also that the language used has an impact on the execution time during a cold start. Generally, Java and C# are slower to initialize that Go, Python or Node.

The diagram below shows how _(B)_ Serverless using AWS Lambda can be used:

![Alt text](img/Architecture-B.png?raw=true "(A) AWS Lambda")



Back-end services running either via AWS ECS containers or AWS Lambdas would provide a "wrapper" layer on top of the Ethereum smart contracts deployed and running in the Ethereum network. The "wrapper" back-end service would be exposed via API of a microservice which serves as the central entry point for all client requests. The application logic hides behind a set of programmatic interfaces, typically a RESTful web services API. This API accepts and processes calls from clients and might implement functionality such as traffic management, request filtering, routing, caching, and authentication and authorization.

For both solutions a SSO services would authenticate and authorise the requests coming via the mobile Apps via a token. The token contains the identity from where the request is coming from and only the requester for the provided Ethereum account owner would be allowed to invoke the back-end services.

The provision for batches of 100 Wallets can be stored at DynamoDB tables and also setting a CloudWatch alarm when it has reached a certain threshold value, of let's say 10 (as suggested).

 
 
 

##### Task 2

To increase the trust users have when making a transaction we would like to compute an
indicator of trust between any two addresses. This indicator could be displayed to a user before
he makes a transaction.

The indicator T works as follows: If A has transferred ETH to B, B has transferred ETH to C,
and C has transferred ETH to D then:
* T[A->B] = 1
* T[A->C] = 2
* T[A->D] = 3

__Objective:__

Write a simple service in Java that, given two addresses X and Y, returns their trust level T. Your service must process all Ethereum blocks (past and incoming) and map them to a format suitable to extract the desired information efficiently.

__Additional Objectives:__

Please pick one or more of the below to implement:

* For privacy or other reasons, the owner of an Ethereum address may not wish for their address to be used when calculating trust scores. Provide the ability for any address owner to exclude their address from calculations.
* Include ERC20 transfers in the trust score.
* Include Wallets such as the one of Task 1 in the trust score.


__Implementation:__

A new Java backend Wallet wrapper was implemented to provide higher level abstraction to calculate the trust indicator from Wallet (_TrustIndicatorWaller.java_).

The data structure used to easily compute the trust indicator and extract the desired information efficiently was using a bespoken implementation of a _Merkle (Hash) tree_ via _TrustIndicatorWaller.trustIndicatorMap_ map.

All past and incoming Ethereum blocks are processed using a _LinkedList_ Java implementation from the hash tree itself.

Addresses can be excluded from trust indicator calculation by setting a flag _trustIndicatorCalculationExclusion_ from the provided _Address.java_ class. 

The Java backend service that interacts with Ethereum smart contracts (Wallet contract) has been implemented via _BlockChainService.java_ class.

An Spring Boot REST API has been exposed via _WalletController.java_ class. 

Below it is listed a few examples of how to invoke REST endpoints:


```
curl http://localhost:8080/info

curl http://localhost:8080/trustIndicatorFromOwnerAddress?addressTo=B

curl http://localhost:8080/trustIndicatorFromTwoAddresses?addressFrom=A&addressTo=B

curl http://localhost:8080/transfer/B -H "Content-Type: application/json" --data '{"value": 10}'

```

A full integration with Swagger2 via SpringFox has been provided. Therefore, alternatively, one can use SwaggerUI to test out the Wallet REST API by simply accessing: "_http://localhost:8080/swagger-ui.html_". 

All available payload _"models"_ can be directly visualised via Swagger as per:


![Alt text](img/swagger-screenshot1.png?raw=true "Wallet models")

The _"wallet-controller"_ endpoints listed via Swagger are:

![Alt text](img/swagger-screenshot2.png?raw=true "Wallet controller endpoints")
 


__Miscellaneous:__

The application is based on Spring Boot framework using Java8 streams for manipulating and calculating the trust indicator. 

Java Lombok library has been used to cut the boilerplace code needed for Java _"@Data"_ class and other design patterns, such as _"@Builder"_ and immutable DTO (Data Transfer Object).

The application can be easily executed via Java cli or inside of an IDE, such as Intellij (_Note:_ for Intellij IDEA, Lombok plugin is required). Alternatively, one can use SwaggerUI that has been provided in order to invoke _WalletController_ endpoints directly. 



