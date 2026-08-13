import {Spinner} from "@/components/Spinner";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {Button} from "@/components/ui/button";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage,} from "@/components/ui/form";
import {Input} from "@/components/ui/input";
import {Textarea} from "@/components/ui/textarea";

import {profileSchema} from "@/features/account/schemas/profileSchema";
import {apiClient} from "@/lib/axiosClient"
import {zodResolver} from "@hookform/resolvers/zod";
import {Camera} from "lucide-react";
import {useEffect, useRef, useState} from "react";
import {useForm} from "react-hook-form";

function getInitials(name = "") {
  return name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join("");
}

/** Profile picture uploader with a live preview. Wire `onFileSelect` to your image endpoint. */
function ProfilePictureField({ name, imageUrl, onFileSelect }) {
  const fileInputRef = useRef(null);
  const [preview, setPreview] = useState(imageUrl);

  useEffect(() => setPreview(imageUrl), [imageUrl]);

  const handleFile = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const objectUrl = URL.createObjectURL(file);
    setPreview(objectUrl);
    onFileSelect?.(file);
  };

  return (
    <div className="flex items-center gap-4">
      <Avatar className="h-16 w-16 border border-border">
        <AvatarImage src={preview} alt={name} />
        <AvatarFallback className="text-base">{getInitials(name)}</AvatarFallback>
      </Avatar>
      <div>
        <Button
          type="button"
          variant="outline"
          size="sm"
          onClick={() => fileInputRef.current?.click()}
        >
          <Camera className="h-3.5 w-3.5" />
          Change photo
        </Button>
        <p className="mt-1.5 text-xs text-muted-foreground">
          JPG or PNG, up to 2MB.
        </p>
        <input
          ref={fileInputRef}
          type="file"
          accept="image/png, image/jpeg"
          className="hidden"
          onChange={handleFile}
        />
      </div>
    </div>
  );
}

/**
 * @param {{ profile: import('@/features/account/types').User, isSaving: boolean, onSave: (values: any) => Promise<void>, onCancel: () => void }} props
 */
export function ProfileForm({ profile, isSaving, onSave, onCancel }) {
  const form = useForm({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      name: profile?.name || "",
      phone: profile?.phone || "",
      bio: profile?.bio || "",
    },
  });

  useEffect(() => {
    form.reset({
      name: profile?.name || "",
      phone: profile?.phone || "",
      bio: profile?.bio || "",
    });
  }, [profile]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleCancel = () => {
    form.reset();
    onCancel?.();
  };

  const handleProfileImage = async (file) => {
      try{
          const formData = new FormData();
          formData.append("image", file);

          await apiClient.post("/users/profile/image", formData);
      }
      catch (error) {
          console.error("Image upload failed:", error);
      }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSave)} className="space-y-6">
        <ProfilePictureField
          name={profile?.name}
          imageUrl={profile?.profileImage}
          onFileSelect={handleProfileImage}
        />

        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Full name</FormLabel>
              <FormControl>
                <Input placeholder="Jane Cooper" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormItem>
          <FormLabel>Email</FormLabel>
          <FormControl>
            <Input value={profile?.email || ""} readOnly disabled />
          </FormControl>
          <FormDescription>Contact support to change your email address.</FormDescription>
        </FormItem>

        <FormField
          control={form.control}
          name="phone"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Phone number</FormLabel>
              <FormControl>
                <Input placeholder="9876543210" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="bio"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Bio</FormLabel>
              <FormControl>
                <Textarea
                  placeholder="Tell us a little about what you do"
                  rows={4}
                  {...field}
                />
              </FormControl>
              <FormDescription>
                {(field.value || "").length}/240 characters
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex justify-end gap-3 pt-2">
          <Button type="button" variant="outline" onClick={handleCancel} disabled={isSaving}>
            Cancel
          </Button>
          <Button type="submit" disabled={isSaving}>
            {isSaving && <Spinner className="text-primary-foreground" />}
            {isSaving ? "Saving..." : "Save changes"}
          </Button>
        </div>
      </form>
    </Form>
  );
}
