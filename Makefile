MAIN_SRC=$(shell find src/main/java -regex '[^._].*\.java$$')
TEST_RES=$(shell cd src/main/resources; find . -type f -a -regex '.*/[^._/][^/]*[^-~]$$')
MAIN_CLS=$(patsubst src/main/java/%,target/classes/%,$(patsubst %.java,%.class,$(MAIN_SRC)))
TEST_SRC=$(shell find src/test/java -regex '[^._].*\.java$$')
TEST_RES=$(shell cd src/test/resources; find . -type f -a -regex '.*/[^._/][^/]*[^-~]$$')
TEST_CLS=$(patsubst src/test/java/%,target/classes/%,$(patsubst %.java,%.class,$(TEST_SRC)))
TESTS=$(subst /,.,$(patsubst target/classes/%.class,%,$(filter %Test.class,$(TEST_CLS))))

JUNIT=$(HOME)/.m2/repository/junit/junit/4.12/junit-4.12.jar:$(HOME)/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar

JAVA=java
JAVAC=javac

qq :
	@echo export TESTS='"'$(TESTS)'"'
	@echo export JUNIT='"'$(JUNIT)'"'
	@echo export MAIN_SRC='"'$(MAIN_SRC)'"'
	@echo export MAIN_RES='"'$(MAIN_RES)'"'
	@echo export MAIN_CLS='"'$(MAIN_CLS)'"'
	@echo export TEST_SRC='"'$(TEST_SRC)'"'
	@echo export TEST_RES='"'$(TEST_RES)'"'
	@echo export TEST_CLS='"'$(TEST_CLS)'"'

target/cfg.jar : $(MAIN_SRC) $(TEST_SRC) $(MAIN_CFG) $(TEST_CFG)
	/bin/rm -rf target/classes/*
	if [ ! -d target/classes ] ; then mkdir -p target/classes ; fi
	javac -target 1.7 -source 1.7 -cp target/classes -d target/classes -s src/main/java $(MAIN_SRC)
	if [ "$(MAIN_RES)" != "" ] ; then tar -C src/main/resources -cf - $(MAIN_RES) | tar -C target/classes -xvf - ; fi
	javac -nowarn -target 1.7 -source 1.7 -cp $(JUNIT):target/classes -d target/classes -s src/test/java $(TEST_SRC)
	if [ "$(TEST_RES)" != "" ] ; then tar -C src/test/resources -cf - $(TEST_RES) | tar -C target/classes -xvf - ; fi
	cd target/classes; jar cvf ../cfg.jar .

test : target/cfg.jar
	java -cp target/cfg.jar:$(JUNIT) org.junit.runner.JUnitCore $(TESTS)

clean :
	/bin/rm -rf target/*.jar target/classes/*

bin/cfg/% : cfg/%
	mkdir -p $$(dirname $@) && cp "$<" "$@"

lib : $(BIN_CFG) $(OBJ)
