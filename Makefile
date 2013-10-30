NAME = Main
JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java
	javac -cp ./vecmath-1.3.1.jar:. *.java
CLASSES = \
        SpriteCanvas.java \
        FaceSprite.java \
        FeetSprite.java \
        OvalSprite.java \
        Sprite.java \
        Main.java 

default: classes 

run: default
	java -cp ./vecmath-1.3.1.jar:. $(NAME)

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class


