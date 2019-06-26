# CryptoFishies

CryptoFishies is a CorDapp for managing fishing rights.

## Running the nodes

Run the `NodeDriver` run configuration from IntelliJ. This starts four nodes:

* `RegulatoryBody`
* `FishermanOne`
* `FishermanTwo`
* `Buyer`

## Front-ends

* `RegulatoryBody`: http://localhost:10007/web/regulatoryBody
* `FishermanOne`: http://localhost:10011/web/fishermanOne
* `FishermanTwo`: http://localhost:10015/web/fishermanTwo
* `Buyer`: http://localhost:10019/web/buyer

The source files for the front-ends can be found under `cordapp/src/main/resources`.

## APIs

* `GET me`
* `GET regulatory-body`
* `GET buyer`
* `GET fishermen`
* `GET other-fishermen`
* `GET buyers`
* `GET cryptofishies`
* `GET consumed-cryptofishies`
* `GET issue-cryptofishy?owner=<INSERT>&type=<INSERT>&location=<INSERT>&quantity=<INSERT>`
* `GET fish-cryptofishy?id=<INSERT>`
* `GET transfer-cryptofishy?id=<INSERT>&newOwner=<INSERT>`

### Example usage

* Regulatory body issues a new CryptoFishy
* Regulatory body transfers the CryptoFishy to the first fisherman
* First fisherman transfers the CryptoFishy to the second fisherman
* Second fisherman fishes the CryptoFishy
* Second fisherman transfers the CryptoFishy to the buyer

## Architecture

This CorDapp has a single state, `CryptoFish`:

    -------------------
    |                 |
    |   CryptoFish    |
    |                 |
    |   - year        |
    |   - owner       |
    |   - type        |
    |   - location    |
    |   - isFished    |
    |                 |    
    -------------------

Initially, `isFished == false`. The state represents the right to fish a fish of the given type in the given location:

    -------------------------------------------------------------------------------------
    |                                                                                   |
    |    - - - - - - - - - -                                     -------------------    |
    |                                              ▲             |                 |    |
    |    |                 |                       | -►          |   CryptoFish    |    |
    |            NO             -------------------     -►       |                 |    |
    |    |                 |    |      Issue command       -►    |    isFished     |    |
    |          INPUTS           |     signed by issuer     -►    |    == false     |    |
    |    |                 |    -------------------     -►       |                 |    |
    |                                              | -►          |                 |    |
    |    - - - - - - - - - -                       ▼             -------------------    |
    |                                                                                   |
    -------------------------------------------------------------------------------------

After fishing the corresponding fish, we update the state to `isFished == true`. The state represents a fish of the given type 
fished in the given location.

    -------------------------------------------------------------------------------------
    |                                                                                   |
    |    -------------------                                     -------------------    |
    |    |                 |                       ▲             |                 |    |
    |    |   CryptoFish    |                       | -►          |   CryptoFish    |    |
    |    |                 |    -------------------     -►       |                 |    |
    |    |    isFished     |    |      Fish command        -►    |    isFished     |    |
    |    |    == false     |    |     signed by fisher     -►    |    == true      |    |
    |    |                 |    -------------------     -►       |                 |    |
    |    |                 |                       | -►          |                 |    |
    |    -------------------                       ▼             -------------------    |
    |                                                                                   |
    -------------------------------------------------------------------------------------

By requiring that a `CryptoFish` state of the correct type and location be transferred whenever a fish is sold, we prevent 
overfishing:

    -------------------------------------------------------------------------------------
    |                                                                                   |
    |    -------------------                                     -------------------    |
    |    |                 |                       ▲             |                 |    |
    |    |   CryptoFish    |                       | -►          |   CryptoFish    |    |
    |    |                 |    -------------------     -►       |                 |    |
    |    |    isFished     |    |  TransferFished command  -►    |    isFished     |    |
    |    |    == true      |    |     signed by owner      -►    |    == true      |    |
    |    |                 |    -------------------     -►       |                 |    |
    |    |                 |                       | -►          |                 |    |
    |    -------------------                       ▼             -------------------    |
    |                                                                                   |
    -------------------------------------------------------------------------------------

This requires three flows:

* `IssueCryptoFishyFlow`
* `FishCryptoFishyFlow`
* `TransferCryptoFishyFlow`
