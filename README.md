
# argent-assessment

##### Task 1


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

Write a simple service in Java that, given two addresses X and Y, returns their trust level T.
Your service must process all Ethereum blocks (past and incoming) and map them to a format
suitable to extract the desired information efficiently.

__Additional Objectives:__

Please pick one or more of the below to implement:

* For privacy or other reasons, the owner of an Ethereum address may not wish for their
address to be used when calculating trust scores. Provide the ability for any address
owner to exclude their address from calculations.
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



