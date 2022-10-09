package am.adrian.global.transactions.requester.constant.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ItemType {

    FILE("file"), IMAGE("image"), VIDEO("video");

    private final String value;
}
