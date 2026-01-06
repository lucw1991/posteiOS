package repository

/*
I figured it may be helpful and make things a little easier later on if we had a single place
where we set access to the repo we are using. Right now it is for MockRepository but we can
swap it to our Api repo whenever we are ready and we shouldn't need to change up our view models
or ui code anywhere.
*/

class RepositoryProvider {

    val repository: MockRepository = MockRepository

}