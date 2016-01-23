"use strict"
// Node.js modules
var net = require('net');
var JsonSocket = require('json-socket');
var extend = require('extend');
var dgram = require('dgram');
var nodeUDP = dgram.createSocket({ type: 'udp4', reuseAddr: true });
var fs = require('fs')
//added
var Js2Xml = require("js2xml").Js2Xml;

var MULTICAST_ADDR = '239.255.255.250';

// global
let employeeDictXml = null;

class Node {

    constructor(HOST, PORT, neighbours, dataFile){
        this._HOST = HOST;
        this._PORT = PORT;
        this._neighbours = neighbours;
        this._dataFile = dataFile;
    }

    get employeeDict(){
        return JSON.parse(fs.readFileSync('./data/' + this._dataFile));
    }

    get host(){
        return this._HOST
    }

    get neighbours(){
        return this._neighbours
    }

    get port(){
        return this._PORT
    }

    // Function that fetches data from neighbour nodes only and appends collected data to employeeDict

    neighboursDataCollection(i) {
        let employeeDict = JSON.parse(fs.readFileSync('./data/' + this._dataFile));
        var socket = new JsonSocket(new net.Socket());
        socket.connect(this._PORT, this._neighbours[i], () => {
            socket.sendMessage({command: 'RequestData'});
            socket.on('message', RequestData => {
                employeeDict.push.apply(employeeDict, RequestData);
            });
        });
    };

    // Function that calculates the average salary of the employees

    averageSalary () {
        var arrSalary = [];
        var arrSalaryInt = [];
        var total = 0;
        let employeeDict = JSON.parse(fs.readFileSync('./data/' + this._dataFile));

        for (var id in employeeDict) {
            arrSalary.push(employeeDict[id]["salary"]);
        }
        for(var i in arrSalary) {
            arrSalaryInt[i] = parseInt(arrSalary[i]);
            total += arrSalaryInt[i];
        }
        var avg = total / arrSalaryInt.length
        return avg;
    }
}

// example: let node = new Node('127.0.0.3', 3000, '["127.0.0.2", "127.0.0.4"]', 'data_node_3.json');

let node = new Node(process.argv[2], process.argv[3], JSON.parse(process.argv[4]), process.argv[5]);

// Listening for multicast messages

nodeUDP.on('listening', () => {
    let address = nodeUDP.address();
    console.log('UDP Node is listening on ' + address.address + ":" + address.port);
});

// Receiving multicast messages, executing commands and sending back data

nodeUDP.on('message', (message, rinfo) => {
    console.log('   Received multicast command "' + message + '" from the client address: '
        + rinfo.address + ':' + rinfo.port );
    if (message == 'hosts/neighbours/employees/salary') {
        let avgSalary = node.averageSalary();
        let numberNeighbours = node.neighbours.length;
        let messageFromNodes = 'Host: '+node.host+' have: '+numberNeighbours+' neighbours and '
        +Object.keys(node.employeeDict).length+' employees and the average salary is '+ avgSalary;
        nodeUDP.send(messageFromNodes, 0, messageFromNodes.length, rinfo.port , rinfo.address, () => {
            console.log('    Sending data back to the client "' + message + '" ('+ messageFromNodes + ')');
        });
    };
});

// Bind our node's UDP connection to a PORT and add membership of the node for multicasting

nodeUDP.bind(node.port, () => {
    nodeUDP.addMembership(MULTICAST_ADDR);
});

/* Function that creates a TCP connection between two or more nodes
or node and client, processes the commands that it receives
and sends the result back */

net.createServer(socket => {
    socket = new JsonSocket(socket);
    socket.on('message', message => {
        if (message.command == 'RequestData') {
            socket.sendMessage(node.employeeDict);
        } else if(message.command == 'MavenRequestData'){
            for (var id in node.neighbours.length) {
                node.neighboursDataCollection(id);
            }
          setTimeout(() => {employeeDictXml = new Js2Xml("MAVEN", node.employeeDict)},400);
          setTimeout(() => {socket.sendMessage(employeeDictXml.toString())},500);
        }
    });
}).listen(node.port, node.host);
console.log('TCP Node is listening on ' + node.host +':'+ node.port);

var nodePort = node.port;
exports.nodePort = nodePort;








