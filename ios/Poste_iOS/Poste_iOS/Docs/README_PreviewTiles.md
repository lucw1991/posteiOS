## Preview tiles that are scrollable but have and end point on both the left and right and do not have animation to move as our Kotlin version does

    - Only file here is PreviewTile
        - Same horizontal style preivews with a picture loaded in from the Assets file. For the pictures I put them in the 2x spot for higher resolution but I think when we integrate on the back end, however we bring in those external picture files would need to end up in the highest resolution setting, be that 3x or whatever else is used for that function. It uses PreviewTile(imageName: ,label:) for each image.
        - The posts and folder buttons above the previews take you to the UIs for each and we will make each preview tile take you to that speicific post or folder in the future if we want as well. It shouldn't be difficult but I think it is fine just taking you to the overall UI as well. 
        
        - What we will need to change:
            - Tile sizing and cropping I would think. We would adjust frame(), scaledToFill(), and .clipped() calls insid PreviewTile.
            - Label Styling if we want. We can adjust the Text(label) modifiers font, padding, and opacity.
            -Tap behavior since the previews are only visual at the moment. Navigation happens in the parent view, HomePage, so we would need to wrap PreviewTile in either a Button or NavigationLink I believe so this should be easy to implement if we choose it.
