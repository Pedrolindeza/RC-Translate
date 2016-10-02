all:
	mkdir -p build/
	javac -d build/ -implicit:class src/rc/translate/g25/*.java

clean:
	rm -rf build/

