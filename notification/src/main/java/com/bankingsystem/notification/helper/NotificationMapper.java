package com.bankingsystem.notification.helper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.bankingsystem.notification.dto.CreateNotificationDto;
import com.bankingsystem.notification.dto.NotificationDto;
import com.bankingsystem.notification.dto.UpdateNotificationReadStatusDto;
import com.bankingsystem.notification.entity.Notification;

//@Mapper(componentModel = "spring")
// public interface NotificationMapper {

//     NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

//     NotificationDto toDto(Notification notification);

//     Notification toEntity(CreateNotificationDto dto);

//     List<NotificationDto> toDtoList(List<Notification> notifications);

// }
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "read", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    Notification toEntity(CreateNotificationDto dto);

    NotificationDto toDto(Notification notification);

    List<NotificationDto> toDtoList(List<Notification> notifications);
}