# Thoughts that came up during implementation 
This file should help reviewer to understand chain of thoughts of developer

1. I considered to use lombok, but for such simple things it'd be an overkill (mostly it'd help to create constructors to inject dependencies). Since we have records in Java17 it doesn't remove as much boilerplate as it used to.
2. I decided to mock just simple repository as HashMap to make it easily replaceable with real repository. Ultimately, I would opt for a file server (S3) and a database with the paths of these files and load it with UrlFileLoader. Or even lower hanging apple - to use InputStream and not store the file at all.
3. I am considering how to handle multiple files which could have conflicting information about some topic. Either I'd clear embedding store before loading new document or first extract only right embedding (the question is how to specify which is right) - that's why I rather like to work on real problem to solve and implement code a little bit "backward"
4. In FileExtension enum I decided to make parser as enum field- initially I considered a strategy pattern, but I think enum approach easier to extend with another file types - just add new value and specify supplier to provide DocumentParser
5. 
