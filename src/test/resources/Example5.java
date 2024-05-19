public class Example {
    protected void example() {
        return new AddFileKeyStoreCommand() {

            @Override
            protected Environment createEnv(OptionSet options, ProcessInfo processInfo) throws UserException {
                return env;
            }
        };
    }
}
