#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>


#define GROUP_NO 25
//#define BUFFER_SIZE 50

int main(int argc, char *argv[]){
    int i;

    //TRS address
    struct sockaddr_in TRS_address;
    TRS_address.sin_family = AF_INET;
    TRS_address.sin_port = htons(59000);
    TRS_address.sin_addr.s_addr = htonl(INADDR_ANY);

    //TCS address
    struct sockaddr_in TCS_address;
    TCS_address.sin_family = AF_INET;
    TCS_address.sin_port = htons(58000+GROUP_NO);
    inet_aton("127.0.0.1", &TCS_address.sin_addr.s_addr);

    //Port
    if(argc >= 2 && argc % 2 == 0){ // #Arguments >= 2
        for(i=1; i<argc; i++){
            if(strcmp(argv[i], "-p") == 0){
                TRS_address.sin_port = htons(atoi(argv[i+1]));
            }
            else if(strcmp(argv[i], "-n") == 0){
                if(inet_aton(argv[i+1], &TCS_address.sin_addr.s_addr) == 0){
                    perror("[ERROR] Invalid IP address for TCS\n");
                    exit(-1);
                }
            }
            else if(strcmp(argv[i], "-e") == 0){
                TCS_address.sin_port = htons(atoi(argv[i+1]));
            }
        }
    }
    else{
        if(argc < 2){
            perror("[ERROR] Language argument must be specified\n");
            exit(-1);
        }
        else{
                perror("[ERROR] Invalid arguments\n");
            exit(-1);
        }
    }

    int TRS_socket, TCS_socket;
    TRS_socket = socket(AF_INET, SOCK_STREAM, 0);
    TCS_socket = socket(AF_INET, SOCK_DGRAM, 0);

    if(TRS_socket == -1 || TCS_socket == -1){
        perror("[ERROR] An error occurred creating TRS and TCS sockets");
    }

    //Bind TRS socket
    if(bind(TRS_socket, (struct sockaddr*)&TRS_address, sizeof(TRS_address)) != 0){
        perror("[ERROR] TRS socket binding error\n");
        exit(-1);
    }

    //Bind TCS socket
    if(bind(TCS_socket, (struct sockaddr*)&TCS_socket, sizeof(TCS_socket)) != 0){
        perror("[ERROR] TCS socket binding error\n");
        exit(-1);
    }

    printf("[INFO] Server listening on port %d\n", ntohl(TRS_address.sin_port));

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

    if(close(TRS_socket) != 0 || close(TCS_socket) != 0){
        perror("[ERROR] Socket closing error\n");
        exit(-1);
    }

    return 0;
}
