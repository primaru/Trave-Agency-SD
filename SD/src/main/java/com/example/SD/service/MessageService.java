package com.example.SD.service;

import com.example.SD.model.Message;
import com.example.SD.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message createMessage(String content) {
        Message newMessage = new Message(content);
        return messageRepository.save(newMessage);
    }

    public Optional<Message> getMessage(Long id) {
        return messageRepository.findById(id);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message updateMessage(Long id, String newContent) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setContent(newContent);
            messageRepository.save(message);
        }
        return message;
    }

    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }
}
