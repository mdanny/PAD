"use strict"

//TCP client
var net = require('net'),
    JsonSocket = require('json-socket');
var _ = require('underscore');
var fs = require('fs');
var dgram = require('dgram');
var clientUDP = dgram.createSocket('udp4');
var libxml = require("libxmljs");
var assert = require("assert");

var MULTICAST_ADDR = '239.255.255.250';
var totalAvgSalary = [];
var mavenHost = '';
var mavenNeighbours = 0;
var mavenEmployees = 0;

//XML schema definition
var xsd = '<xs:schema attributeFormDefault="unqualified" elementFormDefault="qualified" xmlns:xs="http://www.w3.org/2001/XMLSchema"><xs:element name="MAVEN"><xs:complexType><xs:sequence><xs:element name="item" maxOccurs="unbounded" minOccurs="0"><xs:complexType><xs:sequence><xs:element type="xs:string" name="firstName"/><xs:element type="xs:string" name="lastName"/><xs:element type="xs:string" name="departament"/><xs:element type="xs:short" name="salary"/></xs:sequence></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:schema>';

class Client {

    constructor(SRC_PORT, PORT){
        this._SRC_PORT = SRC_PORT
        this._PORT = PORT
    }

    get sourcePort(){
        return this._SRC_PORT
    }

    set sourcePort(newPort){
        if(newPort){
            this._SRC_PORT = newPort;
        }
    }

    get port(){
        return this._PORT;
    }

    set port(newPort){
        if(newPort){
            this._PORT = newPort;
        }
    }

    multicast() {
        var messageToClient = new Buffer('hosts/neighbours/employees/salary');
        console.log('Multicasting the message ' + messageToClient + '\n  to all nodes that are listening on address ' + MULTICAST_ADDR + ':' + this._PORT);
        clientUDP.send(messageToClient, 0, messageToClient.length, this._PORT, MULTICAST_ADDR, () => {
        });
    }

    mavenRequest(){
        console.log('Address of our maven is: '+mavenHost+':'+3000+' ('+mavenNeighbours+' neighbours and '
            +mavenEmployees+' employees)\n');

        // Decorate a standard net.Socket with JsonSocket

        var socket = new JsonSocket(new net.Socket());

        // Establish a connection to maven

        socket.connect(3000, mavenHost, () => {
            socket.sendMessage({command: 'MavenRequestData'});
            socket.on('message', MavenRequestData => {
                console.log('Data received: '+JSON.stringify(MavenRequestData)+'\n');

                fs.writeFile("./savedData.xml", MavenRequestData, (err) => {
                    let xsdDoc = libxml.parseXml(xsd);
                    let xmlDocValid = libxml.parseXml(fs.readFileSync('./savedData.xml').toString());
                    assert.equal(xmlDocValid.validate(xsdDoc), true, 'XML Schema is not validated');
                    console.log('----------------------------------------------------------------------')
                    console.log("Validated XML was saved in savedData.xml in the current directory!");
                    if(err) {
                        return console.log(err);
                    };
                });
            });
        });
    }
}


let client = new Client(6025,3000);

clientUDP.bind(client.sourcePort, () => {
    client.multicast();
    setTimeout(client.mavenRequest, 2000);
    });

clientUDP.on('listening', () => {
    let address = clientUDP.address();
    console.log('UDP client listening on ' + address.address + ': ' + address.port + 'multicast address \n');
    console.log('                      Responses from the nodes                  ');

});

clientUDP.on('message', (mess, rinfo) => {
    let host = String(mess).substring(6, 15);
    let neighbours = parseInt(String(mess).substring(22,24));
    let employees = parseInt(String(mess).substring(39));
    let AvgSalary = parseInt(String(mess).substring(76));

    totalAvgSalary.push(AvgSalary);
    console.log('Host: ' +host+':'+client.port+' has:  ' +neighbours+
        '  neighbours and  '+ employees +'  employees and the average salary is  '+AvgSalary+' ');
     if (neighbours >= mavenNeighbours && employees >= mavenEmployees) {
            mavenHost = host;
            mavenNeighbours = neighbours;
            mavenEmployees = employees;
    };
    console.log('Curent Maven Host: ' +mavenHost+':'+client.port+' has:  ' +mavenNeighbours+ '  neighbours and  '+ mavenEmployees +'  employees');
    console.log('----------------------------------------------------------------------');
});





