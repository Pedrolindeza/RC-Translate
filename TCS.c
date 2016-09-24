#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>

#define GROUP_NO 25
//#define BUFFER_SIZE 50

int main(int argc, char *argv[]){
    int i;
    int port = 58000 + GROUP_NO;

    //Port
    if(argc >= 3){ // #Arguments >= 2
        for(i=1; i<argc; i++){
            if(strcmp(argv[i], "-p") == 0){
                port = atoi(argv[i+1]);
                break;
            }
        }
    }

    int server_socket;
    struct sockaddr_in serveraddr, clientaddr;

    //Socket Creation and Settings
    server_socket = socket(AF_INET, SOCK_DGRAM, 0);
    serveraddr.sin_family=AF_INET;
    serveraddr.sin_addr.s_addr=htonl(INADDR_ANY);
    serveraddr.sin_port=htons(port);

    //Socket bind
    if(bind(server_socket,(struct sockaddr*)&serveraddr,sizeof(serveraddr)) != 0){
        printf("[ERROR] Server socket binding error\n");
        exit(-1);
    }

    printf("[INFO] Server listening on port %d\n", port);

    while(1);
    /*
    char input[BUFFER_SIZE];
    while(1){
        fgets(input, BUFFER_SIZE , stdin);

        if(strncmp(input, "exit", 4) == 0){
            break;
        }
    }
    */

    if(close(server_socket) != 0){
        printf("[ERROR] Server socket closing error\n");
        exit(-1);
    }

    return 0;
}
