package com.example.demo.Operations.Control;

import com.example.demo.Entity.Blog;
import com.example.demo.Operations.Entity.Comments;
import com.example.demo.Operations.Service.CommentServices;
import com.example.demo.Service.BlogServices;
import com.example.demo.Service.UserServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CommentsControl {
    @Autowired
    CommentServices commentServices;
    @Autowired
    UserServices userServices;
    @Autowired
    BlogServices blogServices;

    public Comments findComment(ObjectId id){
        return commentServices.findComment(id);
    }

    public ResponseEntity<?> addComment(Comments comments,Blog blog){
        comments.setBlogID(blog.getId());
        comments.setTime(LocalDateTime.now());
        comments.setLikes(0);
        commentServices.addComment(comments);
        blog.getComments().add(comments);
        blogServices.saveBlog(blog);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> addReply(ObjectId comntId , Comments comments){
        Comments comments1 = commentServices.findComment(comntId);
        if(comments1==null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        ObjectId blogId = comments1.getBlogID();
        Blog blog = blogServices.findBlog(blogId);
        comments.setTime(LocalDateTime.now());
        comments.setBlogID(comments1.getBlogID());
        if(addReplyHelper(blog.getComments(),comntId,comments)){
            blogServices.saveBlog(blog);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Boolean addReplyHelper(List<Comments> commentsList, ObjectId id, Comments target) {
        for (Comments comments : commentsList) {
            if (comments.getCommentId().equals(id)) {
                commentServices.addComment(target);
                comments.getReplies().add(target);
                commentServices.saveComment(comments);
                return true;
            }
            if (comments.getReplies() != null && !comments.getReplies().isEmpty()) {
                boolean found = addReplyHelper(comments.getReplies(), id, target);
                if (found) {
                    return true;
                }
            }
        }
        return false;
    }



    public ResponseEntity<?> deleteComment(ObjectId comntId){
        Comments comments1 = commentServices.findComment(comntId);
        if(comments1==null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        ObjectId blogId =  comments1.getBlogID();
        Blog blog = blogServices.findBlog(blogId);
        if(deleteCommentHelper(blog.getComments(),comntId)){
            blogServices.saveBlog(blog);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    private boolean deleteCommentHelper(List<Comments> commentsList, ObjectId id) {
        for (int i = 0; i < commentsList.size(); i++) {
            Comments comments = commentsList.get(i);
            if (comments.getCommentId().equals(id)) {
                deleteAllChild(comments);
                commentServices.deleteComment(comments.getCommentId());
                commentsList.remove(i);
                return true;
            }
            if (comments.getReplies() != null && !comments.getReplies().isEmpty()) {
                boolean found = deleteCommentHelper(comments.getReplies(), id);
                if (found) {
                    comments.getReplies().removeIf(c -> c.getCommentId().equals(id));
                    commentServices.saveComment(comments);
                    return true;
                }
            }
        }
        return false;
    }

    private void deleteAllChild(Comments comments) {
        if (comments == null) return;

        if (comments.getReplies() != null) {
            for (Comments child : comments.getReplies()) {
                deleteAllChild(child);
                commentServices.deleteComment(child.getCommentId());
            }
            comments.getReplies().clear();
        }
    }


    public ResponseEntity<?> likeComment(ObjectId id){
        Comments comments = commentServices.findComment(id);
        if(comments==null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        ObjectId blogID = comments.getBlogID();
        Blog blog = blogServices.findBlog(blogID);
        if(likeCommentHelper(blog.getComments(),id)){
            blogServices.saveBlog(blog);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean likeCommentHelper(List<Comments>commentsList,ObjectId id){
        for(Comments comments:commentsList){
            if(comments.getCommentId().equals(id)){
                comments.setLikes(comments.getLikes()+1);
                commentServices.saveComment(comments);
                return true;
            }
            if (comments.getReplies() != null && !comments.getReplies().isEmpty()) {
                boolean found = likeCommentHelper(comments.getReplies(), id);
                if (found) {
                    return true;
                }
            }
        }
        return false;
    }



    public ResponseEntity<?> deleteLikeComment(ObjectId id){
        Comments comments = commentServices.findComment(id);
        if(comments==null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        ObjectId blogID = comments.getBlogID();
        Blog blog = blogServices.findBlog(blogID);
        if(deleteLikeCommentHelper(blog.getComments(),id)){
            blogServices.saveBlog(blog);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean deleteLikeCommentHelper(List<Comments>commentsList,ObjectId id){
        for(Comments comments:commentsList){
            if(comments.getCommentId().equals(id)){
                comments.setLikes(comments.getLikes()-1>=0? comments.getLikes():0);
                commentServices.saveComment(comments);
                return true;
            }
            if (comments.getReplies() != null && !comments.getReplies().isEmpty()) {
                boolean found = deleteLikeCommentHelper(comments.getReplies(), id);
                if (found) {
                    return true;
                }
            }
        }
        return false;
    }



    @Transactional
    public ResponseEntity<?> updateComment(ObjectId id , Comments comments){
        Comments comments1 = commentServices.findComment(id);
        if(comments1==null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        ObjectId blogID = comments1.getBlogID();
        Blog blog = blogServices.findBlog(blogID);
        if(updateCommentHelper(blog.getComments(),id,comments)){
            blogServices.saveBlog(blog);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean updateCommentHelper(List<Comments> commentsList,ObjectId id, Comments comments){
        for(Comments comments1: commentsList){
            if(comments1.getCommentId().equals(id)){
                commentServices.UpdateComment(comments,comments1.getCommentId());
                return true;
            }
            if (comments.getReplies() != null && !comments.getReplies().isEmpty()) {
                boolean found = likeCommentHelper(comments.getReplies(), id);
                if (found) {
                    return true;
                }
            }
        }
        return false;
    }
}



