all:
	mkdir -p build/
	javac -cp $CLASSPATH:src/ -d build/ src/rc/*/*/*.java

clean:
	rm -rf build/
