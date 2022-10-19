TEMPLATE = app
CONFIG += console c++11
CONFIG -= app_bundle
CONFIG -= qt

SOURCES += \
        lexer.cpp \
        main.cpp \
        token.cpp

HEADERS += \
    lexer.h \
    token.h
