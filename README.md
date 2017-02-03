# Cfg - Manage secret configuration even with open version control.

## Step 1 - Require the cfg artifact, using a maven `pom.xml` this would be the following dependency:

```
    <dependencies>
      <dependency>
        <groupId>com.github.wmacevoy</groupId>
        <artifactId>cfg</artifactId>
        <version>1.1</version>
      </dependency>
    </dependencies>
```

## Step 2 - Decide on symmetric master dev key(s) to share.

For a small project this is probably one random key you could share via LastPass for example.  If you have access to the cfg jar file, you can generate cryptographically strong patterned keys with

```
java -cp /path/to/cfg.jar cfg.Cfg '$random{PATTERN}'
```
Here PATTERN is a regex-like pattern for the randomness

* `ABC...`  pattern A followed by pattern B, etc.
* `(A|B|...)` a randomly selected pattern A or B, etc.
* `P{min,max}` P repeated between min and max (defaults to min) times.
* `[set]` a random letter from the set (supports ranges)

Some useful examples

* `[A-Za-z0-9]{8-12}` from 8 to 12 alphanumeric characters (ex: `h75dQ0GypEsG`).
* `(rock|paper|scissors)` one random pick (ex: `paper`).
* `[0-9a-f]{4}(:[0-9a-f]{4}){7}` mac-like 128 bit key (ex: `8f38:a082:d902:fb19:3f91:9881:429e:096e`)

## Step 3: Share and set the key.

Set an environment variable with the master key (the name and value does not matter, but you need to share this with the dev team NOT in the repository).

```
export CFG_KEY="8f38:a082:d902:fb19:3f91:9881:429e:096e"
```

## Step 4: Encrypt your application keys

Make note of the output (it will change every time, but all the outputs will decrypt to "db-password")
```
java -cp target/cfg-1.0.jar cfg.Cfg '$encrypt{$env{CFG_KEY},db-password}'
```
## Step 5: Store your encrypted configuration in the resources for your project (src/main/resources/cfg/db.cfg, etc)

```
<db>
  <user>db-user</user>
  <password>$decrypt{$env{CFG_KEY},${cipher}}</password>
  <cipher>2f3da2aa34e978b768f8efb63f36600df5f78f71756f3feb5f7262ab99d008460409f0bef3ac473471964b3e</cipher>
</db>
```

## Step 6 -- load/use configuration.
Assuming the CFG_KEY environment variable is correctly set, and the resources are compiled into your project,

```
Cfg cfg = new Cfg("cfg");
String dbUser = cfg.getString("db/user");
String dbPassword = cfg.getString("db/password");
```

## Step 7 --- Rejoice!

Your configuration (with secret parts encrypted) is now safely managed in through source control and conveniently split into as many components as is useful.

# Functions

* `${path}` -- value of configuration item (supports absolute and relative addressing).
* `$env{VAR}` -- value of environment variable.
* `$raw{path}` -- untranslated value.
* `$encrypt{key,plain}` -- AES-128 GCM with 16-byte padding using the SHA256 hash of key as the encryption key and hex encoded result.
* `$decrypt{key,cipher}` -- reverse of encrypt.
* `$pattern{PATTERN}` -- cryptographically strong random pattern generation.  Supports `ABC...`, `(A|B|C..)`, `[abc]`, `[a-z]`, `A{min,max}` and `A{num}` patterns.
