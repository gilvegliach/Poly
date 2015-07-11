# Poly
Poly is small annotation processor that add 'polymorphic injection' to Dagger 2.
It lets developers write only one injection site in a base class instead of
multiple injection sites, one for each subclass. This is particularly handy for 
Android setups where all activities extend one common base activity.

Dagger 2 works this way: during field injection, when Dagger 2 injects an object,
it first determines the type of the object's reference passed in in the component.
Afterwards, all fields declared in this type and its supertypes will be injected
in this object. The problem is that the object could well be of a more specific
type, so some fields will be skipped.

Poly solves this problem simulating 'polymorphic injection' on the runtime, 
specific type of the object. The idea is simple: gathers all types that can be 
injected from Dagger components and then generate a general `inject()` method which
will check the type of the object and delegate appropriately. 

##Instructions for the samples
The sample represents a simple use case for an Android app. 

1. Build and install processor:

    ```sh
    cd Processor
    mvn clean install
	```
	
2. Build app

    ```sh
    cd Sample
    ./gradlew assemble
	```
