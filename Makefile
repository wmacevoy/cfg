SRC=$(shell find src -regex '[^._].*\.java$$')
OBJ=$(patsubst src/%,bin/%,$(patsubst %.java,%.class,$(SRC)))
CFG=$(shell find cfg -regex '[^._].*\.cfg$$')
BIN_CFG=$(patsubst %,bin/%,$(CFG))
JAVA=java
JAVAC=javac

qq :
	echo src=$(SRC) obj=$(OBJ)

$(OBJ) : $(SRC)
	mkdir -p bin
	$(JAVAC) -cp lib/kiss.jar:bin -d bin -s src $(SRC)

config : $(BIN_CFG)

clean :
	/bin/rm -rf bin/*

bin/cfg/% : cfg/%
	mkdir -p $$(dirname $@) && cp "$<" "$@"

lib : $(BIN_CFG) $(OBJ)

test : lib
	$(JAVA) -cp lib/kiss.jar:bin kiss.util.Run --app cfg.Test
