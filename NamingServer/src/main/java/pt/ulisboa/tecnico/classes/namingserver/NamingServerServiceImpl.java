package pt.ulisboa.tecnico.classes.namingserver;

import static io.grpc.Status.INVALID_ARGUMENT;

import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.*;

import pt.ulisboa.tecnico.classes.contract.naming.NamingServerServiceGrpc;

import io.grpc.stub.StreamObserver;

public class NamingServerServiceImpl extends NamingServerServiceGrpc.NamingServerServiceImplBase{
	private NamingServices service;
	public NamingServerServiceImpl(boolean debug){
		service = new NamingServices(debug);
	}

	/**
	 * When the client calls the register function, the server will call the register function in the
	 * service class, and then return a default response.
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver This is the object that will be used to send the response back to the
	 * client.
	 */
	@Override
	public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        service.register(request.getService(), request.getHost(), request.getQualifiersList());
        RegisterResponse response = RegisterResponse.getDefaultInstance();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

    /**
	 * The function takes a LookupRequest object and returns a LookupResponse object that will be
	 * used by the clients to connect to servers
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver The object that will be used to send the response back to the client.
	 */
	@Override
	public void lookup(LookupRequest request, StreamObserver<LookupResponse> responseObserver) {
        LookupResponse response = LookupResponse.newBuilder().addAllResponse(service.lookup(request.getService(),request.getQualifiersList())).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

    /**
	 * Delete the service and host from the registry. Called to free space in the server list
	 * 
	 * @param request The request object that was sent from the client.
	 * @param responseObserver This is the object that will be used to send the response back to the
	 * client.
	 */
	@Override
	public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
        service.delete(request.getService(), request.getHost());
        DeleteResponse response = DeleteResponse.getDefaultInstance();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}