all:
	mkdir -p build/
	javac -cp src/rc/translate/g25/ -d build/ -implicit:class src/rc/translate/g25/*.java

clean:
	rm -rf build/

